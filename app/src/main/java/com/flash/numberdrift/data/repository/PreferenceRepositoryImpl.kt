package com.flash.numberdrift.data.repository

import android.content.SharedPreferences
import com.flash.numberdrift.domain.repository.PreferenceRepository
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import com.flash.numberdrift.presentation.shared.GameMode

@Singleton
class PreferenceRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : PreferenceRepository {

    companion object {
        private const val KEY_CLASSIC_BEST = "best_score_classic"
        private const val KEY_DRIFT2_BEST = "best_score_drift2"
        private const val KEY_DRIFT1_BEST = "best_score_drift1"
        private const val KEY_SOUND_EFFECTS = "settings_sound_effects"
        private const val KEY_MUSIC = "settings_music"
        private const val KEY_VIBRATION = "settings_vibration"
        private const val KEY_DARK_MODE = "settings_dark_mode"
        private const val KEY_ADS_REMOVED = "ads_removed"
    }

    private fun keyForMode(mode: GameMode): String {
        return when (mode) {
            GameMode.CLASSIC -> KEY_CLASSIC_BEST
            GameMode.DRIFT_2S -> KEY_DRIFT2_BEST
            GameMode.DRIFT_1S -> KEY_DRIFT1_BEST
        }
    }

    override suspend fun getBestScore(mode: GameMode): Int {
        return sharedPreferences.getInt(keyForMode(mode), 0)
    }

    override suspend fun saveBestScore(score: Int, mode: GameMode) {

        val currentBest = getBestScore(mode)

        if (score > currentBest) {
            sharedPreferences
                .edit {
                    putInt(keyForMode(mode), score)
                }
        }
    }

    override suspend fun isSoundEffectsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_SOUND_EFFECTS, true)
    }

    override suspend fun setSoundEffectsEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_SOUND_EFFECTS, enabled) }
    }

    override suspend fun isMusicEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_MUSIC, true)
    }

    override suspend fun setMusicEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_MUSIC, enabled) }
    }

    override suspend fun isVibrationEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_VIBRATION, true)
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(
                KEY_VIBRATION,
                enabled
            )
        }
    }

    override suspend fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false)
    }

    override suspend fun setDarkModeEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(
                KEY_DARK_MODE,
                enabled
            )
        }
    }

    override suspend fun isAdsRemoved(): Boolean {
        return sharedPreferences.getBoolean(KEY_ADS_REMOVED, false)
    }

    override suspend fun setAdsRemoved(removed: Boolean) {
        sharedPreferences.edit {
            putBoolean(
                KEY_ADS_REMOVED,
                removed
            )
        }
    }
}