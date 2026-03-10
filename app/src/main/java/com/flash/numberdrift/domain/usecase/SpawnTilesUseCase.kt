package com.flash.numberdrift.domain.usecase

import com.flash.numberdrift.domain.model.Board
import javax.inject.Inject
import kotlin.random.Random

class SpawnTilesUseCase @Inject constructor() {

    /**
     * Spawns a new tile (2 or 4) in a random empty cell.
     * 90% chance -> 2
     * 10% chance -> 4
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

        val value = if (Random.nextFloat() < 0.9f) 2 else 4

        mutable[row][col] = value

        return Board(
            cells = mutable.map { it.toList() }
        )
    }
}