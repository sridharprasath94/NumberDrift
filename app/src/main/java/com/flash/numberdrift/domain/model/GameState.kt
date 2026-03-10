package com.flash.numberdrift.domain.model

data class GameState (
    val board: Board,
    val score: Int,
    val bestScore: Int
)