package com.flash.numberdrift.presentation.home

sealed interface HomeUiState {

    object Initial : HomeUiState

    object Loading : HomeUiState

    data class Content(
        val bestScore: Int
    ) : HomeUiState
}