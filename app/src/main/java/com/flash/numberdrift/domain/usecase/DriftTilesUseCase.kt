package com.flash.numberdrift.domain.usecase

import com.flash.numberdrift.domain.model.Board

import javax.inject.Inject
import kotlin.random.Random

class DriftTilesUseCase @Inject constructor() {

    /**
     * Moves tiles randomly to adjacent cells (up/down/left/right)
     * if the target cell is empty.
     */
    operator fun invoke(board: Board): Board {

        val size = board.cells.size

        // Convert to mutable board
        val mutableBoard = board.cells.map { it.toMutableList() }.toMutableList()

        for (row in 0 until size) {
            for (col in 0 until size) {

                val value = mutableBoard[row][col]

                if (value == 0) continue

                val directions = listOf(
                    Pair(-1, 0), // up
                    Pair(1, 0),  // down
                    Pair(0, -1), // left
                    Pair(0, 1)   // right
                ).shuffled()

                for ((dx, dy) in directions) {

                    val newRow = row + dx
                    val newCol = col + dy

                    if (newRow in 0 until size &&
                        newCol in 0 until size &&
                        mutableBoard[newRow][newCol] == 0
                    ) {
                        mutableBoard[newRow][newCol] = value
                        mutableBoard[row][col] = 0
                        break
                    }
                }
            }
        }

        return Board(
            cells = mutableBoard.map { it.toList() }
        )
    }
}