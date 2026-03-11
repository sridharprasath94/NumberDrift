package com.flash.numberdrift.presentation.game

import android.os.Build
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flash.numberdrift.domain.model.Direction
import com.flash.numberdrift.domain.usecase.DetectGameOverUseCase
import com.flash.numberdrift.domain.usecase.DriftBoardUseCase
import com.flash.numberdrift.domain.usecase.HasBoardChangedUseCase
import com.flash.numberdrift.domain.usecase.MoveBoardUseCase
import com.flash.numberdrift.domain.usecase.SaveBestScoreUseCase
import com.flash.numberdrift.domain.usecase.SpawnTilesUseCase
import com.flash.numberdrift.domain.usecase.StartGameUseCase
import com.flash.numberdrift.framework.effects.SoundManager
import com.flash.numberdrift.framework.effects.VibrationManager
import com.flash.numberdrift.presentation.shared.GameMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val detectGameOverUseCase: DetectGameOverUseCase,
    private val driftBoardUseCase: DriftBoardUseCase,
    private val moveBoardUseCase: MoveBoardUseCase,
    private val spawnTilesUseCase: SpawnTilesUseCase,
    private val hasBoardChangedUseCase: HasBoardChangedUseCase,
    private val startGameUseCase: StartGameUseCase,
    private val saveBestScoreUseCase: SaveBestScoreUseCase,
    private val vibrationManager: VibrationManager,
    private val soundManager: SoundManager,
) : ViewModel() {

    private val args = GameFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val gameMode = args.gameMode

    private val _uiState =
        MutableStateFlow<GameUiState>(GameUiState.Initial)

    val uiState: StateFlow<GameUiState> = _uiState

    private val driftDelaySeconds: Int? = when (gameMode) {
        GameMode.CLASSIC -> null
        GameMode.DRIFT_2S -> 2
        GameMode.DRIFT_1S -> 1
    }

    private val _driftTimer = MutableStateFlow(driftDelaySeconds ?: 0)
    val driftTimer: StateFlow<Int> = _driftTimer

    private var driftJob: Job? = null

    private fun stopDriftTimer() {
        driftJob?.cancel()
        driftJob = null
    }

    fun saveBestScoreIfNeeded(score: Int) {
        viewModelScope.launch {
            saveBestScoreUseCase.invoke(score, gameMode)
        }
    }

    fun startGame() {
        viewModelScope.launch {

            val state = startGameUseCase(gameMode)

            _uiState.value = GameUiState.Playing(
                board = state.board,
                score = state.score,
                bestScore = state.bestScore,
                gameMode = gameMode
            )
            startDriftTimer()
        }
    }

    fun moveBoard(direction: Direction) {

        val currentState = _uiState.value
        soundManager.playMoveSound()
        if (currentState !is GameUiState.Playing) return

        val (movedBoard, gainedScore) = moveBoardUseCase(
            currentState.board,
            direction
        )
        if (gainedScore > 0) {
            soundManager.playMergeSound()
        }

        // If nothing changed, do nothing
        val boardChanged = hasBoardChangedUseCase(
            currentState.board,
            movedBoard
        )

        if (!boardChanged) {
            val isGameOver = detectGameOverUseCase(currentState.board)
            if (isGameOver) {
                saveBestScoreIfNeeded(currentState.score)
                viewModelScope.launch {
                    soundManager.playGameOverSound()
                }
                stopDriftTimer()
                _uiState.value = GameUiState.GameOver(
                    score = currentState.score,
                    bestScore = maxOf(currentState.bestScore, currentState.score),
                    gameMode = gameMode
                )
            }
            return
        }

        // Spawn new tile
        val boardAfterSpawn = spawnTilesUseCase(movedBoard)

        val newScore = currentState.score + gainedScore

        val isGameOver = detectGameOverUseCase(boardAfterSpawn)

        if (isGameOver) {
            saveBestScoreIfNeeded(newScore)
            soundManager.playGameOverSound()
            stopDriftTimer()
            _uiState.value = GameUiState.GameOver(
                score = newScore,
                bestScore = maxOf(currentState.bestScore, newScore),
                gameMode = gameMode
            )
        } else {
            _uiState.value = GameUiState.Playing(
                board = boardAfterSpawn,
                score = newScore,
                bestScore = currentState.bestScore,
                gameMode = gameMode
            )
        }

        resetDriftTimer()
    }

    private fun startDriftTimer() {

        val delaySeconds = driftDelaySeconds ?: return

        driftJob?.cancel()

        driftJob = viewModelScope.launch {

            while (true) {

                for (i in delaySeconds downTo 1) {
                    _driftTimer.value = i
                    delay(1000)
                }
                _driftTimer.value = 0
                driftBoard()

            }
        }
    }

    private fun resetDriftTimer() {
        startDriftTimer()
    }


    fun driftBoard() {
        val currentState = _uiState.value
        if (currentState !is GameUiState.Playing) return
        val newBoard = driftBoardUseCase(currentState.board)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrationManager.vibrateShort()
        }
        soundManager.playDriftSound()
        val isGameOver = detectGameOverUseCase(newBoard)

        if (isGameOver) {
            saveBestScoreIfNeeded(currentState.score)
            soundManager.playGameOverSound()
            stopDriftTimer()
            _uiState.value = GameUiState.GameOver(
                score = currentState.score,
                bestScore = currentState.bestScore,
                gameMode = gameMode
            )
        } else {
            _uiState.value = currentState.copy(
                board = newBoard
            )
        }
    }

    fun restartGame() {
        startGame()
    }

    override fun onCleared() {
        super.onCleared()
        stopDriftTimer()
    }
}