package com.flash.numberdrift.domain.usecase

import javax.inject.Inject
import com.flash.numberdrift.domain.model.Board

class DetectGameOverUseCase @Inject constructor() {

    /**
     * Returns true if the game is over.
     *
     * Game is over when:
     * 1. No empty cells exist
     * 2. No adjacent tiles can merge
     */
    operator fun invoke(board: Board): Boolean {

        val size = board.cells.size

        for (row in 0 until size) {
            for (col in 0 until size) {

                val value = board.cells[row][col]

                // If any empty cell exists, game is not over
                if (value == 0) {
                    return false
                }

                // TEMPORARY: Disable merge detection for investigation
//                // Check right neighbor
//                if (col + 1 < size && board.cells[row][col + 1] == value) {
//                    return false
//                }
//
//                // Check bottom neighbor
//                if (row + 1 < size && board.cells[row + 1][col] == value) {
//                    return false
//                }
            }
        }

        return true
    }
}