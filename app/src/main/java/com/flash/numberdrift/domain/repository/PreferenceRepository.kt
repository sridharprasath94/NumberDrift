package com.flash.numberdrift.domain.repository

import com.flash.numberdrift.presentation.shared.GameMode

interface PreferenceRepository {

    suspend fun getBestScore(mode: GameMode): Int

    suspend fun saveBestScore(score: Int, mode: GameMode)

}