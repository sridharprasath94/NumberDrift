package com.flash.numberdrift.presentation.game

import android.os.Build
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flash.numberdrift.domain.model.Direction
import com.flash.numberdrift.domain.model.SavedGame
import com.flash.numberdrift.domain.usecase.game.DetectGameOverUseCase
import com.flash.numberdrift.domain.usecase.game.DriftBoardUseCase
import com.flash.numberdrift.domain.usecase.game.HasBoardChangedUseCase
import com.flash.numberdrift.domain.usecase.game.MoveBoardUseCase
import com.flash.numberdrift.domain.usecase.game.SpawnTilesUseCase
import com.flash.numberdrift.domain.usecase.game.StartGameUseCase
import com.flash.numberdrift.domain.usecase.savedgame.ClearSavedGameUseCase
import com.flash.numberdrift.domain.usecase.savedgame.GetSavedGameUseCase
import com.flash.numberdrift.domain.usecase.savedgame.SaveGameUseCase
import com.flash.numberdrift.domain.usecase.score.SaveBestScoreUseCase
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
    private val saveGameUseCase: SaveGameUseCase,
    private val clearSavedGameUseCase: ClearSavedGameUseCase,
    private val getSavedGameUseCase: GetSavedGameUseCase
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

            val savedGame = getSavedGameUseCase(gameMode)

            if (savedGame != null) {
                _uiState.value = GameUiState.Playing(
                    board = savedGame.board,
                    score = savedGame.score,
                    bestScore = savedGame.bestScore,
                    gameMode = savedGame.gameMode
                )
            } else {
                val state = startGameUseCase(gameMode)

                _uiState.value = GameUiState.Playing(
                    board = state.board,
                    score = state.score,
                    bestScore = state.bestScore,
                    gameMode = gameMode
                )
            }

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
                handleGameOver(
                    score = currentState.score,
                    bestScore = maxOf(currentState.bestScore, currentState.score)
                )
            }
            return
        }

        // Spawn new tile
        val boardAfterSpawn = spawnTilesUseCase(movedBoard)

        val newScore = currentState.score + gainedScore

        val isGameOver = detectGameOverUseCase(boardAfterSpawn)

        if (isGameOver) {
            handleGameOver(
                score = newScore,
                bestScore = maxOf(currentState.bestScore, newScore)
            )
        } else {
            val newBestScore = maxOf(currentState.bestScore, newScore)
            _uiState.value = GameUiState.Playing(
                board = boardAfterSpawn,
                score = newScore,
                bestScore = newBestScore,
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
            handleGameOver(
                score = currentState.score,
                bestScore = currentState.bestScore
            )
        } else {
            _uiState.value = currentState.copy(
                board = newBoard
            )
        }
    }

    private fun handleGameOver(score: Int, bestScore: Int) {

        saveBestScoreIfNeeded(score)

        viewModelScope.launch {
            clearSavedGameUseCase(gameMode)
            soundManager.playGameOverSound()
        }

        stopDriftTimer()

        _uiState.value = GameUiState.GameOver(
            score = score,
            bestScore = bestScore,
            gameMode = gameMode
        )
    }

    fun restartGame() {
        val currentState = _uiState.value
        if (currentState !is GameUiState.Playing) return

        saveBestScoreIfNeeded(currentState.score)

        viewModelScope.launch {
            clearSavedGameUseCase(gameMode)
            startGame()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopDriftTimer()
    }

    fun saveGame() {

        val state = _uiState.value

        if (state !is GameUiState.Playing) return
        if (state.score == 0) return

        viewModelScope.launch {

            saveGameUseCase(
                SavedGame(
                    board = state.board,
                    score = state.score,
                    bestScore = state.bestScore,
                    gameMode = state.gameMode,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}