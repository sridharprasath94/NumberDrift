package com.flash.numberdrift.domain.usecase

import com.flash.numberdrift.domain.model.Board
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MoveTilesUseCaseTest {

    private lateinit var useCase: MoveTilesUseCase

    @Before
    fun setup() {
        useCase = MoveTilesUseCase()
    }

    @Test
    fun `tile moves to empty cell`() {

        val board = Board(
            cells = listOf(
                listOf(2, 0),
                listOf(0, 0)
            )
        )

        val (newBoard, score) = useCase(board, 0, 0, 0, 1)

        assertEquals(0, score)
        assertEquals(0, newBoard.cells[0][0])
        assertEquals(2, newBoard.cells[0][1])
    }

    @Test
    fun `tiles merge when values match`() {

        val board = Board(
            cells = listOf(
                listOf(2, 2),
                listOf(0, 0)
            )
        )

        val (newBoard, score) = useCase(board, 0, 0, 0, 1)

        assertEquals(4, score)
        assertEquals(0, newBoard.cells[0][0])
        assertEquals(4, newBoard.cells[0][1])
    }

    @Test
    fun `invalid move returns original board`() {

        val board = Board(
            cells = listOf(
                listOf(2, 4),
                listOf(0, 0)
            )
        )

        val (newBoard, score) = useCase(board, 0, 0, 0, 1)

        assertEquals(0, score)
        assertEquals(board.cells, newBoard.cells)
    }
}