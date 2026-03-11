package com.flash.numberdrift.domain.usecase

import com.flash.numberdrift.domain.repository.PreferenceRepository
import com.flash.numberdrift.presentation.shared.GameSettings
import javax.inject.Inject

class GetGameSettingsUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {

    suspend operator fun invoke(): GameSettings =
        GameSettings(
            soundEffects = preferenceRepository.isSoundEffectsEnabled(),
            music = preferenceRepository.isMusicEnabled(),
            vibration = preferenceRepository.isVibrationEnabled(),
            darkMode = preferenceRepository.isDarkModeEnabled(),
            adsRemoved = preferenceRepository.isAdsRemoved()
        )
}