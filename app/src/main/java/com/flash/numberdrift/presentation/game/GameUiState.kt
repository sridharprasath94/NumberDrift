package com.flash.numberdrift.presentation.game

import com.flash.numberdrift.domain.model.Board

sealed interface GameUiState {

    object  Initial : GameUiState

    object Loading : GameUiState

    data class Playing(
        val board: Board,
        val score: Int,
        val bestScore: Int
    ) : GameUiState

    data class Paused(
        val board: Board,
        val score: Int
    ) : GameUiState

    data class GameOver(
        val score: Int,
        val bestScore: Int
    ) : GameUiState
}