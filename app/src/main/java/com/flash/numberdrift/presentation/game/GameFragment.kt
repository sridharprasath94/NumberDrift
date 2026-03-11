package com.flash.numberdrift.presentation.game

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.Typeface
import android.os.Bundle
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.flash.numberdrift.framework.effects.MusicManager
import com.flash.numberdrift.R
import com.flash.numberdrift.databinding.FragmentGameBinding
import com.flash.numberdrift.domain.model.Board
import com.flash.numberdrift.domain.model.Direction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {
    private val binding: FragmentGameBinding by viewBinding(FragmentGameBinding::bind)

    private val viewModel: GameViewModel by viewModels()

    @Inject
    lateinit var musicManager: MusicManager

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
                    textSize = 26f
                    setTypeface(Typeface.DEFAULT_BOLD) // Bold numbers
                    setTextColor(BLACK)
                    setBackgroundResource(R.drawable.tile_background)
                }

                val params = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    rowSpec = GridLayout.spec(row, 1f)
                    columnSpec = GridLayout.spec(col, 1f)

                    val margin = 16
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
            restartButton.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext(), R.style.WoodDialogTheme)
                    .setTitle("Restart Game")
                    .setMessage("Are you sure you want to restart the current game?")
                    .setNegativeButton("NO") { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton("YES") { _, _ -> viewModel.restartGame() }
                    .show()

            }
        }

        // Override system back press
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    val state = viewModel.uiState.value

                    if (state is GameUiState.Playing) {
                        MaterialAlertDialogBuilder(requireContext(), R.style.WoodDialogTheme)
                            .setTitle("Exit Game")
                            .setMessage("Are you sure you want to leave the current game?")
                            .setNegativeButton("NO") { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton("YES") { _, _ ->
                                viewModel.saveBestScoreIfNeeded(state.score)
                                findNavController().navigateUp()
                            }
                            .show()
                    } else {
                        findNavController().navigate(GameFragmentDirections.actionGameToHome())
                    }
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

                        is GameUiState.Playing -> {
                            renderBoard(state.board)
                            updateScore(state.score, state.bestScore)
                        }

                        is GameUiState.GameOver -> {
                            val dir = GameFragmentDirections.actionGameToGameOver(
                                score = state.score,
                                bestScore = state.bestScore,
                                gameMode = state.gameMode
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
            val gameMode = GameFragmentArgs.fromBundle(requireArguments()).gameMode
            if (gameMode == com.flash.numberdrift.presentation.shared.GameMode.CLASSIC) return@launch

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
                tile.elevation = if (value == 0) 0f else 10f
//                tile.animate()
//                    .scaleX(1.1f)
//                    .scaleY(1.1f)
//                    .setDuration(80)
//                    .withEndAction {
//                        tile.animate().scaleX(1f).scaleY(1f).duration = 80
//                    }
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

    override fun onStart() {
        super.onStart()
        musicManager.startBackgroundMusic()
    }

    override fun onResume() {
        super.onResume()
        musicManager.resume()
    }

    override fun onPause() {
        super.onPause()
        musicManager.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        musicManager.stopBackgroundMusic()
    }
}