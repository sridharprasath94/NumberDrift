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
}