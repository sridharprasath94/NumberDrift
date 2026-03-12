package com.flash.numberdrift.domain.usecase.savedgame

import com.flash.numberdrift.data.repository.FakePreferenceRepositoryImpl
import com.flash.numberdrift.domain.model.Board
import com.flash.numberdrift.domain.model.SavedGame
import com.flash.numberdrift.presentation.shared.GameMode
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class SaveGameUseCaseTest {

    private lateinit var fakeRepository: FakePreferenceRepositoryImpl
    private lateinit var useCase: SaveGameUseCase

    @Before
    fun setup() {
        fakeRepository = FakePreferenceRepositoryImpl()
        useCase = SaveGameUseCase(fakeRepository)
    }

    @Test
    fun `saves game into repository`() = runBlocking {

        val game = SavedGame(
            board = Board(List(5) { List(5) { 0 } }),
            score = 120,
            bestScore = 200,
            gameMode = GameMode.CLASSIC,
            timestamp = System.currentTimeMillis()
        )

        useCase(game)

        val saved = fakeRepository.getSavedGame(GameMode.CLASSIC)

        assertNotNull(saved)
        assertEquals(game, saved)
    }

    @Test
    fun `overwrites existing saved game for same mode`() = runBlocking {

        val firstGame = SavedGame(
            board = Board(List(5) { List(5) { 1 } }),
            score = 50,
            bestScore = 100,
            gameMode = GameMode.CLASSIC,
            timestamp = System.currentTimeMillis()
        )

        val secondGame = SavedGame(
            board = Board(List(5) { List(5) { 2 } }),
            score = 200,
            bestScore = 250,
            gameMode = GameMode.CLASSIC,
            timestamp = System.currentTimeMillis()
        )

        fakeRepository.saveGame(firstGame)

        useCase(secondGame)

        val result = fakeRepository.getSavedGame(GameMode.CLASSIC)

        assertEquals(secondGame, result)
    }
}