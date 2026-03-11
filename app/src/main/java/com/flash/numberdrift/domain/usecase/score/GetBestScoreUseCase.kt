package com.flash.numberdrift.domain.usecase.score

import com.flash.numberdrift.domain.repository.PreferenceRepository
import com.flash.numberdrift.presentation.shared.GameMode
import javax.inject.Inject

class GetBestScoreUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {

    suspend operator fun invoke(mode: GameMode): Int =
        preferenceRepository.getBestScore(mode)
}