package com.flash.numberdrift.domain.usecase.game

import com.flash.numberdrift.domain.model.Board
import com.flash.numberdrift.domain.model.Direction
import com.flash.numberdrift.domain.model.MoveResult
import jakarta.inject.Inject

class ProcessMoveUseCase @Inject constructor(
    private val moveBoardUseCase: MoveBoardUseCase,
    private val spawnTilesUseCase: SpawnTilesUseCase,
    private val hasBoardChangedUseCase: HasBoardChangedUseCase,
    private val detectGameOverUseCase: DetectGameOverUseCase
) {

    operator fun invoke(
        board: Board,
        direction: Direction,
        score: Int,
        bestScore: Int
    ): MoveResult {

        val (movedBoard, gainedScore) =
            moveBoardUseCase(board, direction)

        val changed =
            hasBoardChangedUseCase(board, movedBoard)

        if (!changed) return MoveResult.NoChange

        val boardAfterSpawn =
            spawnTilesUseCase(movedBoard)

        val newScore = score + gainedScore
        val newBest = maxOf(bestScore, newScore)

        val gameOver =
            detectGameOverUseCase(boardAfterSpawn)

        return if (gameOver) {
            MoveResult.GameOver(newScore, newBest)
        } else {
            MoveResult.Playing(
                board = boardAfterSpawn,
                score = newScore,
                bestScore = newBest
            )
        }
    }
}