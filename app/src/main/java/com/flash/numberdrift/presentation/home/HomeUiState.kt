package com.flash.numberdrift.presentation.home

import com.flash.numberdrift.domain.model.Board

sealed interface HomeUiState {

    object Initial : HomeUiState

    object Loading : HomeUiState

    data class Content(
        val bestScore: Int
    ) : HomeUiState
}