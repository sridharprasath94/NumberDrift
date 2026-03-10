package com.flash.numberdrift.domain.usecase

import com.flash.numberdrift.domain.repository.PreferenceRepository
import javax.inject.Inject

class GetBestScoreUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {

    suspend operator fun invoke(): Int =
        preferenceRepository.getBestScore()
}