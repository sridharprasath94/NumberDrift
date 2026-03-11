package com.flash.numberdrift.domain.usecase.game

import com.flash.numberdrift.domain.model.Board
import com.flash.numberdrift.domain.model.Direction
import javax.inject.Inject


class MoveBoardUseCase @Inject constructor() {

    /**
     * Moves the whole board in a direction.
     * Supports LEFT, RIGHT, UP, DOWN.
     */
    operator fun invoke(
        board: Board,
        direction: Direction
    ): Pair<Board, Int> {

        return when (direction) {
            Direction.LEFT -> moveLeft(board)

            Direction.RIGHT -> {
                val reversed = reverseRows(board)
                val (moved, score) = moveLeft(reversed)
                reverseRows(moved) to score
            }

            Direction.UP -> {
                val transposed = transpose(board)
                val (moved, score) = moveLeft(transposed)
                transpose(moved) to score
            }

            Direction.DOWN -> {
                val transposed = transpose(board)
                val reversed = reverseRows(transposed)
                val (moved, score) = moveLeft(reversed)
                transpose(reverseRows(moved)) to score
            }
        }
    }

    private fun moveLeft(board: Board): Pair<Board, Int> {

        val newRows = mutableListOf<List<Int>>()
        var totalScore = 0

        for (row in board.cells) {
            val (mergedRow, score) = mergeRowLeft(row)
            newRows.add(mergedRow)
            totalScore += score
        }

        return Board(newRows) to totalScore
    }

    private fun mergeRowLeft(row: List<Int>): Pair<List<Int>, Int> {

        val filtered = row.filter { it != 0 }.toMutableList()
        val result = mutableListOf<Int>()
        var score = 0
        var i = 0

        while (i < filtered.size) {

            if (i + 1 < filtered.size && filtered[i] == filtered[i + 1]) {
                val merged = filtered[i] * 2
                result.add(merged)
                score += merged
                i += 2
            } else {
                result.add(filtered[i])
                i++
            }
        }

        while (result.size < row.size) {
            result.add(0)
        }

        return result to score
    }

    private fun reverseRows(board: Board): Board {
        val newCells = board.cells.map { it.reversed() }
        return Board(newCells)
    }

    private fun transpose(board: Board): Board {

        val size = board.cells.size
        val newCells = MutableList(size) { MutableList(size) { 0 } }

        for (row in 0 until size) {
            for (col in 0 until size) {
                newCells[col][row] = board.cells[row][col]
            }
        }

        return Board(newCells.map { it.toList() })
    }
}