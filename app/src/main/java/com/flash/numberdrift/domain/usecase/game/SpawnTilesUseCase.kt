package com.flash.numberdrift.domain.usecase.game

import com.flash.numberdrift.domain.model.Board
import javax.inject.Inject

class SpawnTilesUseCase @Inject constructor() {

    /**
     * Spawns a new tile (2) in a random empty cell.
     */
    operator fun invoke(board: Board): Board {

        val size = board.cells.size
        val mutable = board.cells.map { it.toMutableList() }.toMutableList()

        val emptyCells = mutableListOf<Pair<Int, Int>>()

        for (row in 0 until size) {
            for (col in 0 until size) {
                if (mutable[row][col] == 0) {
                    emptyCells.add(row to col)
                }
            }
        }

        if (emptyCells.isEmpty()) {
            return board
        }

        val (row, col) = emptyCells.random()

        mutable.sumOf { rowCells -> rowCells.count { it != 0 } }
        mutable[row][col] = 2

        return Board(
            cells = mutable.map { it.toList() }
        )
    }
}