package com.flash.numberdrift.domain.usecase

import com.flash.numberdrift.domain.repository.PreferenceRepository
import com.flash.numberdrift.presentation.shared.GameMode
import javax.inject.Inject

class SaveBestScoreUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {

    suspend operator fun invoke(score: Int, mode: GameMode) {
        val currentBest = preferenceRepository.getBestScore(mode)
        if (score > currentBest) {
            preferenceRepository.saveBestScore(score, mode)
        }
    }
}