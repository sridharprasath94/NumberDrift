package com.flash.numberdrift.presentation.game

import androidx.lifecycle.ViewModel
import com.flash.numberdrift.domain.usecase.DetectGameOverUseCase
import com.flash.numberdrift.domain.usecase.DriftBoardUseCase
import com.flash.numberdrift.domain.usecase.MoveBoardUseCase
import com.flash.numberdrift.domain.usecase.StartGameUseCase
import com.flash.numberdrift.domain.usecase.HasBoardChangedUseCase
import com.flash.numberdrift.domain.usecase.SpawnTilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import com.flash.numberdrift.domain.model.Direction
import com.flash.numberdrift.domain.usecase.SaveBestScoreUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val detectGameOverUseCase: DetectGameOverUseCase,
    private val driftBoardUseCase: DriftBoardUseCase,
    private val moveBoardUseCase: MoveBoardUseCase,
    private val spawnTilesUseCase: SpawnTilesUseCase,
    private val hasBoardChangedUseCase: HasBoardChangedUseCase,
    private val startGameUseCase: StartGameUseCase,
    private val saveBestScoreUseCase: SaveBestScoreUseCase,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<GameUiState>(GameUiState.Initial)

    val uiState: StateFlow<GameUiState> = _uiState

    private val _driftTimer = MutableStateFlow(3)
    val driftTimer: StateFlow<Int> = _driftTimer

    private val driftDelaySeconds = 3

    private var driftJob: Job? = null

    private fun saveBestScoreIfNeeded(score: Int) {
        viewModelScope.launch {
            saveBestScoreUseCase.invoke(score)
        }
    }

    fun startGame() {
        viewModelScope.launch {

            _uiState.value = GameUiState.Loading

            val state = startGameUseCase()

            _uiState.value = GameUiState.Playing(
                board = state.board,
                score = state.score,
                bestScore = state.bestScore
            )
            startDriftTimer()
        }
    }

    fun moveBoard(direction: Direction) {

        val currentState = _uiState.value

        if (currentState !is GameUiState.Playing) return

        val (movedBoard, gainedScore) = moveBoardUseCase(
            currentState.board,
            direction
        )

        // If nothing changed, do nothing
        val boardChanged = hasBoardChangedUseCase(
            currentState.board,
            movedBoard
        )

        if (!boardChanged) {
            val isGameOver = detectGameOverUseCase(currentState.board)
            if (isGameOver) {
                saveBestScoreIfNeeded(currentState.score)
                _uiState.value = GameUiState.GameOver(
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
            saveBestScoreIfNeeded(newScore)
            _uiState.value = GameUiState.GameOver(
                score = newScore,
                bestScore = maxOf(currentState.bestScore, newScore)
            )
        } else {
            _uiState.value = GameUiState.Playing(
                board = boardAfterSpawn,
                score = newScore,
                bestScore = currentState.bestScore
            )
        }

        resetDriftTimer()
    }

    private fun startDriftTimer() {

        driftJob?.cancel()

        driftJob = viewModelScope.launch {

            while (true) {

                for (i in driftDelaySeconds downTo 1) {
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

        val isGameOver = detectGameOverUseCase(newBoard)

        if (isGameOver) {
            saveBestScoreIfNeeded(currentState.score)
            _uiState.value = GameUiState.GameOver(
                score = currentState.score,
                bestScore = currentState.bestScore
            )
        } else {
            _uiState.value = currentState.copy(
                board = newBoard
            )
        }
    }
}