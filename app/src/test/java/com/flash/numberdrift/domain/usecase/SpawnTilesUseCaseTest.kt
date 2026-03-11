package com.flash.numberdrift.domain.usecase

import com.flash.numberdrift.domain.model.Board
import com.flash.numberdrift.domain.usecase.game.SpawnTilesUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SpawnTilesUseCaseTest {

    private lateinit var useCase: SpawnTilesUseCase

    @Before
    fun setup() {
        useCase = SpawnTilesUseCase()
    }

    @Test
    fun `spawns exactly one tile`() {

        val board = Board(
            cells = listOf(
                listOf(2,0,0),
                listOf(0,0,0),
                listOf(0,0,0)
            )
        )

        val newBoard = useCase(board)

        val originalCount = board.cells.flatten().count { it != 0 }
        val newCount = newBoard.cells.flatten().count { it != 0 }

        assertEquals(originalCount + 1, newCount)
    }

    @Test
    fun `spawned tile is either two or four`() {

        val board = Board(
            cells = listOf(
                listOf(0,0,0),
                listOf(0,0,0),
                listOf(0,0,0)
            )
        )

        val newBoard = useCase(board)

        val values = newBoard.cells.flatten().filter { it != 0 }

        assertEquals(1, values.size)
        assertTrue(values[0] == 2 || values[0] == 4)
    }

    @Test
    fun `board unchanged if no empty cells`() {

        val board = Board(
            cells = listOf(
                listOf(2,4,8),
                listOf(16,32,64),
                listOf(128,256,512)
            )
        )

        val newBoard = useCase(board)

        assertEquals(board.cells, newBoard.cells)
    }
}