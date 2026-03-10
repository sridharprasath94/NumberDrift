package com.flash.numberdrift.domain.usecase

import javax.inject.Inject
import com.flash.numberdrift.domain.model.Board

class MoveTilesUseCase @Inject constructor() {

    /**
     * Moves a tile from (fromRow, fromCol) to (toRow, toCol).
     *
     * Rules:
     * - If target cell is empty → move tile
     * - If target cell has same value → merge tiles
     * - Otherwise → no move
     *
     * Returns:
     * Pair<NewBoard, ScoreGained>
     */
    operator fun invoke(
        board: Board,
        fromRow: Int,
        fromCol: Int,
        toRow: Int,
        toCol: Int
    ): Pair<Board, Int> {

        val mutableBoard = board.cells.map { it.toMutableList() }.toMutableList()

        val sourceValue = mutableBoard[fromRow][fromCol]
        val targetValue = mutableBoard[toRow][toCol]

        if (sourceValue == 0) {
            return board to 0
        }

        // Move to empty cell
        if (targetValue == 0) {
            mutableBoard[toRow][toCol] = sourceValue
            mutableBoard[fromRow][fromCol] = 0
            return Board(cells = mutableBoard.map { it.toList() }) to 0
        }

        // Merge tiles if values match
        if (sourceValue == targetValue) {
            val mergedValue = sourceValue + targetValue
            mutableBoard[toRow][toCol] = mergedValue
            mutableBoard[fromRow][fromCol] = 0
            return Board(cells = mutableBoard.map { it.toList() }) to mergedValue
        }

        // Invalid move
        return board to 0
    }
}