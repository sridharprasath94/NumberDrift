package com.flash.numberdrift.domain.model


data class Board(
    val cells: List<List<Int>>
) {
    val size: Int
        get() = cells.size

    fun get(row: Int, col: Int): Int {
        return cells[row][col]
    }
}