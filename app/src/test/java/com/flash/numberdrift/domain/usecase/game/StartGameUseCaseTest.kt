package com.flash.numberdrift.domain.usecase.game

import com.flash.numberdrift.data.repository.FakePreferenceRepositoryImpl
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
    private lateinit var fakeRepository: PreferenceRepository


    @Before
    fun setup() {
        fakeRepository = FakePreferenceRepositoryImpl()
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
        fakeRepository.saveBestScore(50, GameMode.CLASSIC)
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