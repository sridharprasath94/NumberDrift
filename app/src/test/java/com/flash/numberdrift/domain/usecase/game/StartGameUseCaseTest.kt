package com.flash.numberdrift.domain.usecase.game

import com.flash.numberdrift.domain.model.SavedGame

import com.flash.numberdrift.domain.repository.PreferenceRepository
import com.flash.numberdrift.presentation.shared.GameMode
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StartGameUseCaseTest {

    private lateinit var useCase: StartGameUseCase

    // Fake repository for testing
    private val fakeRepository = object : PreferenceRepository {
        override suspend fun getBestScore(mode: GameMode): Int = 50
        override suspend fun saveBestScore(score: Int, mode: GameMode) {}

        override suspend fun isSoundEffectsEnabled(): Boolean = true
        override suspend fun setSoundEffectsEnabled(enabled: Boolean) {}

        override suspend fun isMusicEnabled(): Boolean = true
        override suspend fun setMusicEnabled(enabled: Boolean) {}

        override suspend fun isVibrationEnabled(): Boolean = true
        override suspend fun setVibrationEnabled(enabled: Boolean) {}

        override suspend fun isDarkModeEnabled(): Boolean = false
        override suspend fun setDarkModeEnabled(enabled: Boolean) {}

        override suspend fun saveGame(game: SavedGame) {}

        override suspend fun getSavedGame(mode: GameMode): SavedGame? = null

        override suspend fun clearSavedGame(mode: GameMode) {}

        override suspend fun hasSavedGame(mode: GameMode): Boolean = false

        override suspend fun isAdsRemoved(): Boolean = false

        override suspend fun setAdsRemoved(removed: Boolean) {}
    }

    @Before
    fun setup() {
        useCase = StartGameUseCase(
            SpawnTilesUseCase(),
            fakeRepository
        )
    }

    @Test
    fun `game starts with score zero`() = runBlocking {
        val state = useCase(GameMode.CLASSIC)
        assertEquals(0, state.score)
    }

    @Test
    fun `best score is loaded from repository`() = runBlocking {
        val state = useCase(GameMode.CLASSIC)
        assertEquals(50, state.bestScore)
    }

    @Test
    fun `board size is five by five`() = runBlocking {
        val state = useCase(GameMode.CLASSIC)
        assertEquals(5, state.board.cells.size)
        assertEquals(5, state.board.cells[0].size)
    }

    @Test
    fun `board starts with exactly two tiles`() = runBlocking {
        val state = useCase(GameMode.CLASSIC)
        val tileCount = state.board.cells.flatten().count { it != 0 }
        assertEquals(2, tileCount)
    }

    @Test
    fun `spawned tiles are either two or four`() = runBlocking {
        val state = useCase(GameMode.CLASSIC)
        val values = state.board.cells.flatten().filter { it != 0 }
        assertTrue(values.all { it == 2 || it == 4 })
    }
}