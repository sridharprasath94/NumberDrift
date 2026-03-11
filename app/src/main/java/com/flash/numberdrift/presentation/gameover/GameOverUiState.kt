package com.flash.numberdrift.presentation.gameover

sealed interface GameOverUiState {

    object Initial : GameOverUiState

    object Loading : GameOverUiState

    data class Content(
        val bestScore: Int
    ) : GameOverUiState
}