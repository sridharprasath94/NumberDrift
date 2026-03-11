package com.flash.numberdrift.domain.usecase.game

import com.flash.numberdrift.domain.model.Board
import javax.inject.Inject

/**
 * Checks whether the board has changed after a move.
 * This prevents spawning new tiles when the move had no effect.
 */
class HasBoardChangedUseCase @Inject constructor() {

    operator fun invoke(oldBoard: Board, newBoard: Board): Boolean {
        return oldBoard.cells != newBoard.cells
    }
}