package com.flash.numberdrift.domain.usecase.savedgame

import com.flash.numberdrift.domain.repository.PreferenceRepository
import com.flash.numberdrift.presentation.shared.GameMode
import jakarta.inject.Inject

class ClearSavedGameUseCase @Inject constructor(
    private val repository: PreferenceRepository
) {

    suspend operator fun invoke(mode: GameMode) {
        repository.clearSavedGame(mode)
    }
}