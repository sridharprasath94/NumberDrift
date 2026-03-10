package com.flash.numberdrift.domain.usecase

import com.flash.numberdrift.domain.repository.PreferenceRepository
import javax.inject.Inject

class SaveBestScoreUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {

    suspend operator fun invoke(score: Int) {
        val currentBest = preferenceRepository.getBestScore()
        if (score > currentBest) {
            preferenceRepository.saveBestScore(score)
        }
    }
}