package com.flash.numberdrift.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flash.numberdrift.domain.usecase.GetBestScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameOverViewModel @Inject constructor(
    private val getBestScoreUseCase: GetBestScoreUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<GameOverUiState> =
        MutableStateFlow(GameOverUiState.Initial)

    val uiState: StateFlow<GameOverUiState> = _uiState

    init {
        loadBestScore()
    }

    private fun loadBestScore() {

        _uiState.value = GameOverUiState.Loading

        viewModelScope.launch {
            val bestScore = getBestScoreUseCase()

            _uiState.value = GameOverUiState.Content(
                bestScore = bestScore
            )
        }
    }
}