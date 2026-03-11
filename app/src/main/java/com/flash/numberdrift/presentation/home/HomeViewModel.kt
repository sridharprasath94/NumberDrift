package com.flash.numberdrift.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flash.numberdrift.domain.usecase.GetBestScoreUseCase
import com.flash.numberdrift.presentation.shared.GameMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBestScoreUseCase: GetBestScoreUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.Initial)

    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadBestScores()
    }

    private fun loadBestScores() {
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch {
            _uiState.value = HomeUiState.Content(
                bestScoreClassicMode = getBestScoreUseCase(GameMode.CLASSIC),
                bestScoreDrift2Mode = getBestScoreUseCase(GameMode.DRIFT_2S),
                bestScoreDrift1Mode = getBestScoreUseCase(GameMode.DRIFT_1S)
            )
        }
    }
}