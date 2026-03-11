package com.flash.numberdrift.presentation.home

sealed interface HomeUiState {

    object Initial : HomeUiState

    object Loading : HomeUiState

    data class Content(
        val bestScoreClassicMode: Int,
        val bestScoreDrift2Mode: Int,
        val bestScoreDrift1Mode: Int,

        val savedClassicScore: Int?,
        val savedDrift2Score: Int?,
        val savedDrift1Score: Int?
    ) : HomeUiState
}