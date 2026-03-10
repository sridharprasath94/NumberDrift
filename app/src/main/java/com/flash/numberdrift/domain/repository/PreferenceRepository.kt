package com.flash.numberdrift.domain.repository

interface PreferenceRepository {

    suspend fun getBestScore(): Int

    suspend fun saveBestScore(score: Int)

}