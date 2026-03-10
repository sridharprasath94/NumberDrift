package com.flash.numberdrift.domain.usecase

import com.flash.numberdrift.domain.model.Board
import com.flash.numberdrift.domain.model.Direction
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MoveBoardUseCaseTest {

    private lateinit var useCase: MoveBoardUseCase

    @Before
    fun setup() {
        useCase = MoveBoardUseCase()
    }

    @Test
    fun `move left shifts tiles`() {

        val board = Board(
            cells = listOf(
                listOf(0,2,0,0),
                listOf(0,0,0,0),
                listOf(0,0,0,0),
                listOf(0,0,0,0)
            )
        )

        val (newBoard, _) = useCase(board, Direction.LEFT)

        assertEquals(2, newBoard.cells[0][0])
    }

    @Test
    fun `move left merges tiles`() {

        val board = Board(
            cells = listOf(
                listOf(2,2,0,0),
                listOf(0,0,0,0),
                listOf(0,0,0,0),
                listOf(0,0,0,0)
            )
        )

        val (newBoard, score) = useCase(board, Direction.LEFT)

        assertEquals(4, newBoard.cells[0][0])
        assertEquals(4, score)
    }

    @Test
    fun `tiles compact correctly`() {

        val board = Board(
            cells = listOf(
                listOf(2,0,2,0),
                listOf(0,0,0,0),
                listOf(0,0,0,0),
                listOf(0,0,0,0)
            )
        )

        val (newBoard, _) = useCase(board, Direction.LEFT)

        assertEquals(4, newBoard.cells[0][0])
    }
    @Test
    fun `move right shifts tiles`() {

        val board = Board(
            cells = listOf(
                listOf(0,0,2,0),
                listOf(0,0,0,0),
                listOf(0,0,0,0),
                listOf(0,0,0,0)
            )
        )

        val (newBoard, _) = useCase(board, Direction.RIGHT)

        assertEquals(2, newBoard.cells[0][3])
    }

    @Test
    fun `move up shifts tiles`() {

        val board = Board(
            cells = listOf(
                listOf(0,0,0,0),
                listOf(2,0,0,0),
                listOf(0,0,0,0),
                listOf(0,0,0,0)
            )
        )

        val (newBoard, _) = useCase(board, Direction.UP)

        assertEquals(2, newBoard.cells[0][0])
    }

    @Test
    fun `move down shifts tiles`() {

        val board = Board(
            cells = listOf(
                listOf(0,0,0,0),
                listOf(2,0,0,0),
                listOf(0,0,0,0),
                listOf(0,0,0,0)
            )
        )

        val (newBoard, _) = useCase(board, Direction.DOWN)

        assertEquals(2, newBoard.cells[3][0])
    }
}