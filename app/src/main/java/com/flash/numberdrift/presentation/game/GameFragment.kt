package com.flash.numberdrift.presentation.game

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import com.flash.numberdrift.R
import com.flash.numberdrift.databinding.FragmentGameBinding
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.view.Gravity
import android.widget.TextView
import android.widget.GridLayout
import com.flash.numberdrift.domain.model.Board
import com.flash.numberdrift.domain.model.Direction

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs
import android.annotation.SuppressLint
import android.graphics.Color.*
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {
    private val binding: FragmentGameBinding by viewBinding(FragmentGameBinding::bind)

    private val viewModel: GameViewModel by viewModels()

    private lateinit var gestureDetector: GestureDetector

    private lateinit var tiles: Array<Array<TextView>>
    private fun setupBoard(size: Int) {

        val grid = binding.gameGrid
        grid.rowCount = size
        grid.columnCount = size

        tiles = Array(size) { row ->
            Array(size) { col ->

                val tile = TextView(requireContext()).apply {
                    gravity = Gravity.CENTER
                    textSize = 18f
                    setTextColor(BLACK)
                    setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
                }

                val params = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    rowSpec = GridLayout.spec(row, 1f)
                    columnSpec = GridLayout.spec(col, 1f)

                    val margin = 12
                    setMargins(margin, margin, margin, margin)
                }

                tile.layoutParams = params
                grid.addView(tile)

                tile
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Initialize TextSwitcher for drift countdown
        binding.driftTimerText.setFactory {
            TextView(requireContext()).apply {
                textSize = 32f
                gravity = Gravity.CENTER
                setTextColor(WHITE)
            }
        }

        observeState()

        // Start the game when fragment loads
        viewModel.startGame()

        gestureDetector = GestureDetector(
            requireContext(),
            object : GestureDetector.SimpleOnGestureListener() {

                private val SWIPE_THRESHOLD = 100
                private val SWIPE_VELOCITY_THRESHOLD = 100

                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {

                    val diffX = e2.x - (e1?.x ?: 0f)
                    val diffY = e2.y - (e1?.y ?: 0f)

                    if (abs(diffX) > abs(diffY)) {
                        if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                viewModel.moveBoard(Direction.RIGHT)
                            } else {
                                viewModel.moveBoard(Direction.LEFT)
                            }
                            return true
                        }
                    } else {
                        if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffY > 0) {
                                viewModel.moveBoard(Direction.DOWN)
                            } else {
                                viewModel.moveBoard(Direction.UP)
                            }
                            return true
                        }
                    }

                    return false
                }
            }
        )


        @SuppressLint("ClickableViewAccessibility")
        binding.gameGrid.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)

            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
            }

            true
        }

        with(binding) {

            // Temporary navigation for testing the navigation flow
            shuffleButton.setOnClickListener {

                // TODO: Replace this with drift mechanic trigger
                viewModel.driftBoard()

                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.gameFragment, true)
                    .build()

                findNavController().navigate(
                    R.id.action_game_to_gameOver,
                    null,
                    navOptions
                )
            }
        }

        // Override system back press to go to Home instead of Game
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_game_to_home)
                }
            }
        )
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    when (state) {

                        GameUiState.Initial -> {
                            // nothing yet
                        }

                        GameUiState.Loading -> {
                            // TODO show loading animation if needed
                        }

                        is GameUiState.Playing -> {
                            renderBoard(state.board)
                            updateScore(state.score, state.bestScore)
                        }

                        is GameUiState.GameOver -> {
                            val dir = GameFragmentDirections.actionGameToGameOver(
                                score = state.score,
                                bestScore = state.bestScore
                            )

                            findNavController().navigate(dir)

                        }

                        is GameUiState.Paused -> {
                            // TODO pause overlay if implemented
                        }
                    }
                }
            }

        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.driftTimer.collect { seconds ->

                    val switcher = binding.driftTimerText

                    val text = if (seconds > 0) {
                        seconds.toString()
                    } else {
                        ""
                    }

                    switcher.setText(text)

                    // Shake animation when drift happens
                    if (seconds == 0) {
                        binding.gameGrid.animate()
                            .translationX(10f)
                            .setDuration(50)
                            .withEndAction {

                                if (!isAdded || view == null) return@withEndAction

                                binding.gameGrid.animate()
                                    .translationX(-10f)
                                    .setDuration(50)
                                    .withEndAction {

                                        if (!isAdded || view == null) return@withEndAction

                                        binding.gameGrid.animate()
                                            .translationX(0f).duration = 50
                                    }
                            }
                    }
                }
            }

        }
    }

    private fun renderBoard(board: Board) {
        val size = board.cells.size

        if (!::tiles.isInitialized) {
            setupBoard(size)
        }

        for (row in 0 until size) {
            for (col in 0 until size) {

                val value = board.cells[row][col]

                val tile = tiles[row][col]
                tile.text = if (value == 0) "" else value.toString()
                tile.setBackgroundColor(getTileColor(value))
            }
        }
    }

    private fun updateScore(score: Int, bestScore: Int) {
        binding.scoreText.text = getString(R.string.score_format, score)
        binding.bestScoreText.text = getString(R.string.best_score_format, bestScore)
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