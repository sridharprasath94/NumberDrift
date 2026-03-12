package com.flash.numberdrift.presentation.game.board

import android.content.Context
import android.graphics.Color.BLACK
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.flash.numberdrift.R
import com.flash.numberdrift.domain.model.Board

data class TileRow(
    val tiles: List<TextView>
)

class BoardRenderer(
    private val grid: GridLayout,
    private val context: Context
) {

    private companion object {
        const val TILE_MARGIN = 16
        const val BASE_TEXT_SIZE = 26f
    }

    private var rows: Array<TileRow> = emptyArray()

    fun setup(size: Int) {

        grid.rowCount = size
        grid.columnCount = size

        rows = Array(size) { row ->
            val rowTiles = MutableList(size) { col ->
                val tile = createTile(row, col)
                grid.addView(tile)
                tile
            }
            TileRow(rowTiles)
        }
    }

    private fun createTile(row: Int, col: Int): TextView {

        val tile = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, BASE_TEXT_SIZE)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(BLACK)

            maxLines = 1
            includeFontPadding = false
            isSingleLine = true
            ellipsize = null
            letterSpacing = -0.02f

            setBackgroundResource(R.drawable.tile_background)
        }

        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = 0
            rowSpec = GridLayout.spec(row, 1f)
            columnSpec = GridLayout.spec(col, 1f)
            setMargins(TILE_MARGIN, TILE_MARGIN, TILE_MARGIN, TILE_MARGIN)
        }

        tile.layoutParams = params

        return tile
    }

    fun render(board: Board) {

        val size = board.cells.size

        if (rows.isEmpty()) {
            setup(size)
        }

        val currentRows = rows

        for (row in 0 until size) {
            for (col in 0 until size) {

                val value = board.cells[row][col]
                val tile = currentRows[row].tiles[col]

                val text = if (value == 0) "" else value.toString()
                tile.text = text

                tile.textSize = getTileTextSize(text)

                tile.setBackgroundColor(getTileColor(value))
                tile.elevation = if (value == 0) 0f else 10f
            }
        }
    }

    private fun getTileTextSize(value: String): Float {
        val digits = value.length
        val result = when (digits) {
            1, 2 -> 26f
            3 -> 22f
            4 -> 20f
            5 -> 18f
            else -> 12f
        }
        return result
    }

    private fun getTileColor(value: Int): Int {
        return when (value) {
            0 -> "#CDC1B4".toColorInt()
            2 -> "#EEE4DA".toColorInt()
            4 -> "#EDE0C8".toColorInt()
            8 -> "#F2B179".toColorInt()
            16 -> "#F59563".toColorInt()
            32 -> "#F67C5F".toColorInt()
            64 -> "#F65E3B".toColorInt()
            128 -> "#EDCF72".toColorInt()
            256 -> "#EDCC61".toColorInt()
            512 -> "#EDC850".toColorInt()
            1024 -> "#EDC53F".toColorInt()
            2048 -> "#EDC22E".toColorInt()
            else -> "#3C3A32".toColorInt()
        }
    }
}