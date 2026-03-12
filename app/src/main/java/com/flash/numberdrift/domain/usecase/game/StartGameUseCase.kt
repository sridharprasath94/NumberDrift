package com.flash.numberdrift.domain.usecase.game

import com.flash.numberdrift.domain.model.Board
import com.flash.numberdrift.domain.model.GameState
import com.flash.numberdrift.domain.repository.PreferenceRepository
import com.flash.numberdrift.presentation.shared.GameMode
import javax.inject.Inject

class StartGameUseCase @Inject constructor(
    private val spawnTilesUseCase: SpawnTilesUseCase,
    private val preferenceRepository: PreferenceRepository
) {

    suspend operator fun invoke(mode: GameMode): GameState {

        val size = 5

        var board = Board(
            cells = MutableList(size) {
                MutableList(size) { 0 }
            }.map { it.toList() }
        )

        repeat(2) {
            board = spawnTilesUseCase(board)
        }

        val bestScore = preferenceRepository.getBestScore(mode)

        return GameState(
            board = board,
            score = 0,
            bestScore = bestScore
        )
    }
}