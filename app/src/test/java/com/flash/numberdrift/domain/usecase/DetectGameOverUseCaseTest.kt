package com.flash.numberdrift.domain.usecase

import com.flash.numberdrift.domain.model.Board
import com.flash.numberdrift.domain.usecase.game.DetectGameOverUseCase
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DetectGameOverUseCaseTest {

    private lateinit var detectGameOverUseCase: DetectGameOverUseCase

    @Before
    fun setup() {
        detectGameOverUseCase = DetectGameOverUseCase()
    }

    @Test
    fun `game is NOT over when board has empty cells`() {

        val board = Board(
            cells = listOf(
                listOf(2, 4, 8, 16),
                listOf(4, 0, 16, 2),
                listOf(8, 16, 2, 4),
                listOf(16, 2, 4, 8)
            )
        )

        val result = detectGameOverUseCase(board)

        assertFalse(result)
    }

    @Test
    fun `game is NOT over when adjacent tiles can merge`() {

        val board = Board(
            cells = listOf(
                listOf(2, 2, 8, 16),
                listOf(4, 8, 16, 2),
                listOf(8, 16, 2, 4),
                listOf(16, 2, 4, 8)
            )
        )

        val result = detectGameOverUseCase(board)

        assertFalse(result)
    }

    @Test
    fun `game IS over when board is full and no merges possible`() {

        val board = Board(
            cells = listOf(
                listOf(2, 4, 8, 16),
                listOf(4, 8, 16, 2),
                listOf(8, 16, 2, 4),
                listOf(16, 2, 4, 8)
            )
        )

        val result = detectGameOverUseCase(board)

        assertTrue(result)
    }
}