package com.flash.numberdrift.domain.usecase

import com.flash.numberdrift.domain.repository.PreferenceRepository
import com.flash.numberdrift.domain.usecase.game.StartGameUseCase
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

        override suspend fun getBestScore(mode: GameMode): Int {
            return 50
        }

        override suspend fun saveBestScore(score: Int, mode: GameMode) {
            // no-op for tests
        }
    }

    @Before
    fun setup() {
        useCase = StartGameUseCase(fakeRepository)
    }

    @Test
    fun `game starts with score zero`() = runBlocking {

        val state = useCase()

        assertEquals(0, state.score)
    }

    @Test
    fun `best score is loaded from repository`() = runBlocking {

        val state = useCase()

        assertEquals(50, state.bestScore)
    }

    @Test
    fun `board size is five by five`() = runBlocking {

        val state = useCase()

        assertEquals(5, state.board.cells.size)
        assertEquals(5, state.board.cells[0].size)
    }

    @Test
    fun `board starts with exactly two tiles`() = runBlocking {

        val state = useCase()

        val tileCount = state.board.cells.flatten().count { it != 0 }

        assertEquals(2, tileCount)
    }

    @Test
    fun `spawned tiles are either two or four`() = runBlocking {

        val state = useCase()

        val values = state.board.cells.flatten().filter { it != 0 }

        assertTrue(values.all { it == 2 || it == 4 })
    }
}