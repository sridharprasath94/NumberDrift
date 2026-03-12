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

class HasSavedGameUseCaseTest {

    private lateinit var fakeRepository: FakePreferenceRepositoryImpl
    private lateinit var useCase: HasSavedGameUseCase

    @Before
    fun setup() {
        fakeRepository = FakePreferenceRepositoryImpl()
        useCase = HasSavedGameUseCase(fakeRepository)
    }

    @Test
    fun `returns false when no saved game exists`() = runBlocking {

        val result = useCase(GameMode.CLASSIC)

        assertFalse(result)
    }

    @Test
    fun `returns true when a saved game exists`() = runBlocking {

        val savedGame = SavedGame(
            board = Board(List(5) { List(5) { 0 } }),
            score = 100,
            bestScore = 150,
            gameMode = GameMode.CLASSIC,
            timestamp = System.currentTimeMillis()
        )

        fakeRepository.saveGame(savedGame)

        val result = useCase(GameMode.CLASSIC)

        assertTrue(result)
    }
}