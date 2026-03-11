package com.flash.numberdrift.domain.model

import com.flash.numberdrift.presentation.shared.GameMode

data class SavedGame(
    val board: Board,
    val score: Int,
    val bestScore: Int,
    val gameMode: GameMode,
    val timestamp: Long
)