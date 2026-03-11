package com.flash.numberdrift.domain.usecase

import com.flash.numberdrift.domain.model.Board
import com.flash.numberdrift.domain.usecase.game.HasBoardChangedUseCase
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HasBoardChangedUseCaseTest {

    private lateinit var useCase: HasBoardChangedUseCase

    @Before
    fun setup() {
        useCase = HasBoardChangedUseCase()
    }

    @Test
    fun `returns false when boards are identical`() {

        val board1 = Board(
            cells = listOf(
                listOf(2, 0),
                listOf(0, 4)
            )
        )

        val board2 = Board(
            cells = listOf(
                listOf(2, 0),
                listOf(0, 4)
            )
        )

        val result = useCase(board1, board2)

        assertFalse(result)
    }

    @Test
    fun `returns true when boards differ`() {

        val board1 = Board(
            cells = listOf(
                listOf(2, 0),
                listOf(0, 4)
            )
        )

        val board2 = Board(
            cells = listOf(
                listOf(0, 2),
                listOf(0, 4)
            )
        )

        val result = useCase(board1, board2)

        assertTrue(result)
    }
}