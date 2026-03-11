package com.flash.numberdrift.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flash.numberdrift.domain.usecase.savedgame.ClearSavedGameUseCase
import com.flash.numberdrift.domain.usecase.score.GetBestScoreUseCase
import com.flash.numberdrift.domain.usecase.savedgame.GetSavedGameUseCase
import com.flash.numberdrift.presentation.shared.GameMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBestScoreUseCase: GetBestScoreUseCase,
    private val clearSavedGameUseCase: ClearSavedGameUseCase,
    private val getSavedGameUseCase: GetSavedGameUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.Initial)

    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        refreshBestScores()
    }

    fun refreshBestScores() {
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch {
            val savedClassic = getSavedGameUseCase(GameMode.CLASSIC)
            val savedDrift2 = getSavedGameUseCase(GameMode.DRIFT_2S)
            val savedDrift1 = getSavedGameUseCase(GameMode.DRIFT_1S)

            _uiState.value = HomeUiState.Content(
                bestScoreClassicMode = getBestScoreUseCase(GameMode.CLASSIC),
                bestScoreDrift2Mode = getBestScoreUseCase(GameMode.DRIFT_2S),
                bestScoreDrift1Mode = getBestScoreUseCase(GameMode.DRIFT_1S),
                savedClassicScore = savedClassic?.score,
                savedDrift2Score = savedDrift2?.score,
                savedDrift1Score = savedDrift1?.score
            )
        }
    }

    fun clearSavedGame(mode: GameMode) {
        viewModelScope.launch {
            clearSavedGameUseCase(mode)
            refreshBestScores()
        }
    }
}