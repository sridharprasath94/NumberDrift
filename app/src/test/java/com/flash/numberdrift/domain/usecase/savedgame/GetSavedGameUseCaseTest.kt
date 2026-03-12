package com.flash.numberdrift.domain.usecase.savedgame

import com.flash.numberdrift.data.repository.FakePreferenceRepositoryImpl
import com.flash.numberdrift.domain.model.Board
import com.flash.numberdrift.domain.model.SavedGame
import com.flash.numberdrift.presentation.shared.GameMode
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetSavedGameUseCaseTest {

    private lateinit var fakeRepository: FakePreferenceRepositoryImpl
    private lateinit var useCase: GetSavedGameUseCase

    @Before
    fun setup() {
        fakeRepository = FakePreferenceRepositoryImpl()
        useCase = GetSavedGameUseCase(fakeRepository)
    }

    @Test
    fun `returns null when no saved game exists`() = runBlocking {

        val result = useCase(GameMode.CLASSIC)

        assertNull(result)
    }

    @Test
    fun `returns saved game when it exists`() = runBlocking {

        val savedGame = SavedGame(
            board = Board(List(5) { List(5) { 0 } }),
            score = 120,
            bestScore = 200,
            gameMode = GameMode.CLASSIC,
            timestamp = System.currentTimeMillis()
        )

        fakeRepository.saveGame(savedGame)

        val result = useCase(GameMode.CLASSIC)

        assertEquals(savedGame, result)
    }

    @Test
    fun `returns saved game for correct mode only`() = runBlocking {

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

        val result = useCase(GameMode.DRIFT_2S)

        assertEquals(driftGame, result)
    }
}