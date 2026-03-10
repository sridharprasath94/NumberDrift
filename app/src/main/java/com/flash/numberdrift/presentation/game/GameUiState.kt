package com.flash.numberdrift.presentation.game

sealed interface GameUiState {

    object  Initial : GameUiState

    object Loading : GameUiState

    data class Playing(
        val board: List<List<Int>>,
        val score: Int,
        val bestScore: Int
    ) : GameUiState

    data class Paused(
        val board: List<List<Int>>,
        val score: Int
    ) : GameUiState

    data class GameOver(
        val score: Int,
        val bestScore: Int
    ) : GameUiState
}