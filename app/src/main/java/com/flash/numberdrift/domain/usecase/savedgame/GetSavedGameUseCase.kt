package com.flash.numberdrift.domain.usecase.savedgame

import com.flash.numberdrift.domain.model.SavedGame
import com.flash.numberdrift.domain.repository.PreferenceRepository
import com.flash.numberdrift.presentation.shared.GameMode
import jakarta.inject.Inject

class GetSavedGameUseCase @Inject constructor(
    private val repository: PreferenceRepository
) {

    suspend operator fun invoke(mode: GameMode): SavedGame? {
        return repository.getSavedGame(mode)
    }
}