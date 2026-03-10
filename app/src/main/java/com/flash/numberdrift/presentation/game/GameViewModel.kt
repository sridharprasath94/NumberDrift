package com.flash.numberdrift.presentation.game

import androidx.lifecycle.ViewModel
import com.flash.numberdrift.domain.usecase.DetectGameOverUseCase
import com.flash.numberdrift.domain.usecase.DriftTilesUseCase
import com.flash.numberdrift.domain.usecase.MoveTilesUseCase
import com.flash.numberdrift.domain.usecase.StartGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val detectGameOverUseCase: DetectGameOverUseCase,
    private val driftTilesUseCase: DriftTilesUseCase,
    private val moveTilesUseCase: MoveTilesUseCase,
    private val startGameUseCase: StartGameUseCase,

    ) : ViewModel() {

    private val _uiState =
        MutableStateFlow<GameUiState>(GameUiState.Initial)

    val uiState: StateFlow<GameUiState> = _uiState

}