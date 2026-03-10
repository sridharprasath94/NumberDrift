package com.flash.numberdrift.data.repository

import android.content.SharedPreferences
import com.flash.numberdrift.domain.repository.PreferenceRepository
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class PreferenceRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : PreferenceRepository {

    companion object {
        private const val KEY_BEST_SCORE = "best_score"
    }

    override suspend fun getBestScore(): Int {
        return sharedPreferences.getInt(KEY_BEST_SCORE, 0)
    }

    override suspend fun saveBestScore(score: Int) {

        val currentBest = getBestScore()

        if (score > currentBest) {
            sharedPreferences
                .edit {
                    putInt(KEY_BEST_SCORE, score)
                }
        }
    }
}