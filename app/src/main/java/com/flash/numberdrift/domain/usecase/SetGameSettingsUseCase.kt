package com.flash.numberdrift.domain.usecase

import com.flash.numberdrift.domain.repository.PreferenceRepository
import com.flash.numberdrift.presentation.shared.GameSettings
import javax.inject.Inject


class SetGameSettingsUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {

    suspend operator fun invoke(gameSettings: GameSettings): GameSettings {
        preferenceRepository.setSoundEnabled(gameSettings.sound)
        preferenceRepository.setVibrationEnabled(gameSettings.vibration)
        preferenceRepository.setDarkModeEnabled(gameSettings.darkMode)
        preferenceRepository.setAdsRemoved(gameSettings.adsRemoved)
        return gameSettings
    }
}