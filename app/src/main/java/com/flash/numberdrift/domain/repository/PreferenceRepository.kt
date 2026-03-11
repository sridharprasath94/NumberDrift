package com.flash.numberdrift.domain.repository

import com.flash.numberdrift.presentation.shared.GameMode

interface PreferenceRepository {

    suspend fun getBestScore(mode: GameMode): Int

    suspend fun saveBestScore(score: Int, mode: GameMode)

    suspend fun isSoundEnabled(): Boolean
    suspend fun setSoundEnabled(enabled: Boolean)

    suspend fun isVibrationEnabled(): Boolean
    suspend fun setVibrationEnabled(enabled: Boolean)

    suspend fun isDarkModeEnabled(): Boolean
    suspend fun setDarkModeEnabled(enabled: Boolean)


    /// TODO - Temporary solution, should be replaced with a more robust system for handling purchases and subscriptions
    suspend fun isAdsRemoved(): Boolean
    suspend fun setAdsRemoved(removed: Boolean)
}