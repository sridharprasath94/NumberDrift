package com.flash.numberdrift.domain.usecase.game

import com.flash.numberdrift.domain.model.Board
import com.flash.numberdrift.domain.model.Direction
import com.flash.numberdrift.domain.model.MoveResult
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class ProcessMoveUseCaseTest {

    private lateinit var moveBoardUseCase: MoveBoardUseCase
    private lateinit var spawnTilesUseCase: SpawnTilesUseCase
    private lateinit var hasBoardChangedUseCase: HasBoardChangedUseCase
    private lateinit var detectGameOverUseCase: DetectGameOverUseCase

    private lateinit var useCase: ProcessMoveUseCase

    private val initialBoard = Board(
        cells = List(5) { List(5) { 0 } }
    )

    @Before
    fun setup() {
        moveBoardUseCase = mock()
        spawnTilesUseCase = mock()
        hasBoardChangedUseCase = mock()
        detectGameOverUseCase = mock()

        useCase = ProcessMoveUseCase(
            moveBoardUseCase,
            spawnTilesUseCase,
            hasBoardChangedUseCase,
            detectGameOverUseCase
        )
    }

    @Test
    fun `returns NoChange when board does not change`() {

        whenever(moveBoardUseCase(initialBoard, Direction.LEFT))
            .thenReturn(initialBoard to 0)

        whenever(hasBoardChangedUseCase(initialBoard, initialBoard))
            .thenReturn(false)

        val result = useCase(
            board = initialBoard,
            direction = Direction.LEFT,
            score = 0,
            bestScore = 0
        )

        assertTrue(result is MoveResult.NoChange)

        verify(spawnTilesUseCase, never()).invoke(any())
        verify(detectGameOverUseCase, never()).invoke(any())
    }

    @Test
    fun `returns Playing when board changes and game is not over`() {

        val movedBoard = initialBoard.copy()
        val spawnedBoard = initialBoard.copy()

        whenever(moveBoardUseCase(initialBoard, Direction.LEFT))
            .thenReturn(movedBoard to 10)

        whenever(hasBoardChangedUseCase(initialBoard, movedBoard))
            .thenReturn(true)

        whenever(spawnTilesUseCase(movedBoard))
            .thenReturn(spawnedBoard)

        whenever(detectGameOverUseCase(spawnedBoard))
            .thenReturn(false)

        val result = useCase(
            board = initialBoard,
            direction = Direction.LEFT,
            score = 10,
            bestScore = 20
        )

        assertTrue(result is MoveResult.Playing)

        val playing = result as MoveResult.Playing

        assertEquals(spawnedBoard, playing.board)
        assertEquals(20, playing.score)
        assertEquals(20, playing.bestScore)
    }

    @Test
    fun `returns GameOver when board changes and no moves remain`() {

        val movedBoard = initialBoard.copy()
        val spawnedBoard = initialBoard.copy()

        whenever(moveBoardUseCase(initialBoard, Direction.LEFT))
            .thenReturn(movedBoard to 5)

        whenever(hasBoardChangedUseCase(initialBoard, movedBoard))
            .thenReturn(true)

        whenever(spawnTilesUseCase(movedBoard))
            .thenReturn(spawnedBoard)

        whenever(detectGameOverUseCase(spawnedBoard))
            .thenReturn(true)

        val result = useCase(
            board = initialBoard,
            direction = Direction.LEFT,
            score = 10,
            bestScore = 12
        )

        assertTrue(result is MoveResult.GameOver)

        val gameOver = result as MoveResult.GameOver

        assertEquals(15, gameOver.score)
        assertEquals(15, gameOver.bestScore)
    }
}