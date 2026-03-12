package com.flash.numberdrift.domain.model

sealed class MoveResult {

    object NoChange : MoveResult()

    data class Playing(
        val board: Board,
        val score: Int,
        val bestScore: Int
    ) : MoveResult()

    data class GameOver(
        val score: Int,
        val bestScore: Int
    ) : MoveResult()
}