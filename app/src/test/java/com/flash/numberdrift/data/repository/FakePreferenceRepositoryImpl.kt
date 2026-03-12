package com.flash.numberdrift.data.repository

import com.flash.numberdrift.domain.model.SavedGame
import com.flash.numberdrift.domain.repository.PreferenceRepository
import com.flash.numberdrift.presentation.shared.GameMode


class FakePreferenceRepositoryImpl : PreferenceRepository {

    private val bestScores = mutableMapOf<GameMode, Int>()

    private val savedGames = mutableMapOf<GameMode, SavedGame>()

    var soundEffectsEnabled: Boolean = true
    var musicEnabled: Boolean = true
    var vibrationEnabled: Boolean = true
    var darkModeEnabled: Boolean = false
    var adsRemoved: Boolean = false

    override suspend fun getBestScore(mode: GameMode): Int =
        bestScores[mode] ?: 0

    override suspend fun saveBestScore(score: Int, mode: GameMode) {
        bestScores[mode] = score
    }

    override suspend fun isSoundEffectsEnabled(): Boolean = soundEffectsEnabled
    override suspend fun setSoundEffectsEnabled(enabled: Boolean) {
        soundEffectsEnabled = enabled
    }

    override suspend fun isMusicEnabled(): Boolean = musicEnabled
    override suspend fun setMusicEnabled(enabled: Boolean) {
        musicEnabled = enabled
    }

    override suspend fun isVibrationEnabled(): Boolean = vibrationEnabled
    override suspend fun setVibrationEnabled(enabled: Boolean) {
        vibrationEnabled = enabled
    }

    override suspend fun isDarkModeEnabled(): Boolean = darkModeEnabled
    override suspend fun setDarkModeEnabled(enabled: Boolean) {
        darkModeEnabled = enabled
    }

    override suspend fun saveGame(game: SavedGame) {
        savedGames[game.gameMode] = game
    }

    override suspend fun getSavedGame(mode: GameMode): SavedGame? =
        savedGames[mode]

    override suspend fun clearSavedGame(mode: GameMode) {
        savedGames.remove(mode)
    }

    override suspend fun hasSavedGame(mode: GameMode): Boolean =
        savedGames.containsKey(mode)

    override suspend fun isAdsRemoved(): Boolean = adsRemoved

    override suspend fun setAdsRemoved(removed: Boolean) {
        adsRemoved = removed
    }
}