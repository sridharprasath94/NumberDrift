package com.flash.numberdrift.domain.usecase

import com.flash.numberdrift.domain.model.Board

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DriftTilesUseCaseTest {

    private lateinit var driftTilesUseCase: DriftTilesUseCase

    @Before
    fun setup() {
        driftTilesUseCase = DriftTilesUseCase()
    }

    @Test
    fun `board size remains the same after drift`() {

        val board = Board(
            cells = listOf(
                listOf(2, 0, 0),
                listOf(0, 4, 0),
                listOf(0, 0, 8)
            )
        )

        val result = driftTilesUseCase(board)

        assertEquals(board.cells.size, result.cells.size)
        assertEquals(board.cells[0].size, result.cells[0].size)
    }

    @Test
    fun `tile values are preserved after drift`() {

        val board = Board(
            cells = listOf(
                listOf(2, 0, 0),
                listOf(0, 4, 0),
                listOf(0, 0, 8)
            )
        )

        val result = driftTilesUseCase(board)

        val originalValues = board.cells.flatten().filter { it != 0 }.sorted()
        val newValues = result.cells.flatten().filter { it != 0 }.sorted()

        assertEquals(originalValues, newValues)
    }

    @Test
    fun `tiles only move to empty cells`() {

        val board = Board(
            cells = listOf(
                listOf(2, 0, 0),
                listOf(0, 4, 0),
                listOf(0, 0, 8)
            )
        )

        val result = driftTilesUseCase(board)

        // ensure no new tiles are created
        val originalCount = board.cells.flatten().count { it != 0 }
        val newCount = result.cells.flatten().count { it != 0 }

        assertEquals(originalCount, newCount)

        // ensure all values remain valid
        assertTrue(result.cells.flatten().all { it == 0 || it == 2 || it == 4 || it == 8 })
    }
}