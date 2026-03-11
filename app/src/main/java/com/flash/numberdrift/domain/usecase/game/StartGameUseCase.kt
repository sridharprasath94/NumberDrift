package com.flash.numberdrift.domain.usecase.game

import com.flash.numberdrift.domain.model.Board
import com.flash.numberdrift.domain.model.GameState
import com.flash.numberdrift.domain.repository.PreferenceRepository
import com.flash.numberdrift.presentation.shared.GameMode
import javax.inject.Inject
import kotlin.random.Random

class StartGameUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {

    suspend operator fun invoke(mode: GameMode): GameState {

        val size = 5

        val board = MutableList(size) {
            MutableList(size) { 0 }
        }

        repeat(2) {
            spawnTile(board)
        }

        val bestScore = preferenceRepository.getBestScore(mode)

        return GameState(
            board = Board(cells = board.map { it.toList() }),
            score = 0,
            bestScore = bestScore
        )
    }

    private fun spawnTile(board: MutableList<MutableList<Int>>) {

        val emptyCells = mutableListOf<Pair<Int, Int>>()

        for (row in board.indices) {
            for (col in board[row].indices) {
                if (board[row][col] == 0) {
                    emptyCells.add(row to col)
                }
            }
        }

        if (emptyCells.isEmpty()) return

        val (row, col) = emptyCells.random()

        board[row][col] = if (Random.nextFloat() < 0.9f) 2 else 4
    }
}