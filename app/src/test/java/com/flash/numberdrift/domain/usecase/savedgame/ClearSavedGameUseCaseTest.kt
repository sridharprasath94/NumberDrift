package com.flash.numberdrift.domain.usecase.savedgame

import com.flash.numberdrift.data.repository.FakePreferenceRepositoryImpl
import com.flash.numberdrift.domain.model.Board
import com.flash.numberdrift.domain.model.SavedGame
import com.flash.numberdrift.presentation.shared.GameMode
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ClearSavedGameUseCaseTest {

    private lateinit var fakeRepository: FakePreferenceRepositoryImpl
    private lateinit var clearSavedGameUseCase: ClearSavedGameUseCase
    private lateinit var hasSavedGameUseCase: HasSavedGameUseCase

    @Before
    fun setup() {
        fakeRepository = FakePreferenceRepositoryImpl()
        clearSavedGameUseCase = ClearSavedGameUseCase(fakeRepository)
        hasSavedGameUseCase = HasSavedGameUseCase(fakeRepository)
    }

    @Test
    fun `clears saved game for the given mode`() = runBlocking {

        val savedGame = SavedGame(
            board = Board(List(5) { List(5) { 0 } }),
            score = 100,
            bestScore = 200,
            gameMode = GameMode.CLASSIC,
            timestamp = System.currentTimeMillis()
        )

        fakeRepository.saveGame(savedGame)

        assertTrue(hasSavedGameUseCase(GameMode.CLASSIC))

        clearSavedGameUseCase(GameMode.CLASSIC)

        assertFalse(hasSavedGameUseCase(GameMode.CLASSIC))
    }

    @Test
    fun `does not affect saved games of other modes`() = runBlocking {

        val classicGame = SavedGame(
            board = Board(List(5) { List(5) { 0 } }),
            score = 100,
            bestScore = 200,
            gameMode = GameMode.CLASSIC,
            timestamp = System.currentTimeMillis()
        )

        val driftGame = SavedGame(
            board = Board(List(5) { List(5) { 0 } }),
            score = 150,
            bestScore = 250,
            gameMode = GameMode.DRIFT_2S,
            timestamp = System.currentTimeMillis()
        )

        fakeRepository.saveGame(classicGame)
        fakeRepository.saveGame(driftGame)

        clearSavedGameUseCase(GameMode.CLASSIC)

        assertFalse(hasSavedGameUseCase(GameMode.CLASSIC))
        assertTrue(hasSavedGameUseCase(GameMode.DRIFT_2S))
    }
}