package com.flash.numberdrift.domain.usecase.savedgame

import com.flash.numberdrift.domain.model.SavedGame
import com.flash.numberdrift.domain.repository.PreferenceRepository
import jakarta.inject.Inject

class SaveGameUseCase @Inject constructor(
    private val repository: PreferenceRepository
) {

    suspend operator fun invoke(game: SavedGame) {
        repository.saveGame(game)
    }
}