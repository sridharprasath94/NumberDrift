package com.flash.numberdrift.presentation.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.flash.numberdrift.R
import com.flash.numberdrift.databinding.FragmentGameBinding
import com.flash.numberdrift.framework.effects.MusicManager
import com.flash.numberdrift.presentation.game.board.BoardRenderer
import com.flash.numberdrift.presentation.game.input.SwipeGestureHandler
import com.flash.numberdrift.presentation.shared.GameMode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {
    private val binding: FragmentGameBinding by viewBinding(FragmentGameBinding::bind)

    private val viewModel: GameViewModel by viewModels()

    private val args: GameFragmentArgs by navArgs()

    @Inject
    lateinit var musicManager: MusicManager

    private val gestureDetector: GestureDetector by lazy(LazyThreadSafetyMode.NONE) {
        SwipeGestureHandler(requireContext()) { direction ->
            viewModel.moveBoard(direction)
        }.detector
    }

    private val boardRenderer: BoardRenderer by lazy(LazyThreadSafetyMode.NONE) {
        BoardRenderer(binding.gameGrid, requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Initialize TextSwitcher for drift countdown
        binding.driftTimerText.setFactory {
            TextView(requireContext()).apply {
                textSize = 32f
                gravity = Gravity.CENTER
            }
        }

        observeGameState()
        observeDriftTimer()

        // Start the game when fragment loads
        viewModel.startGame()

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
                showRestartDialog()
            }
        }

        // Override system back press
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    val state = viewModel.uiState.value

                    if (state is GameUiState.Playing) {
                        showExitDialog(state.score)
                    } else {
                        findNavController().navigate(GameFragmentDirections.actionGameToHome())
                    }
                }
            }
        )
    }

    private fun observeGameState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun observeDriftTimer() {
        viewLifecycleOwner.lifecycleScope.launch {

            if (args.gameMode == GameMode.CLASSIC) return@launch

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.driftTimer.collect { seconds ->
                    updateDriftTimer(seconds)
                }
            }
        }
    }

    private fun renderState(state: GameUiState) {
        when (state) {

            GameUiState.Initial -> {}

            is GameUiState.Playing -> {
                boardRenderer.render(state.board)
                updateScore(state.score, state.bestScore)
            }

            is GameUiState.GameOver -> {
                navigateToGameOver(state)
            }

            is GameUiState.Paused -> {}
        }
    }

    private fun updateDriftTimer(seconds: Int) {
        val switcher = binding.driftTimerText
        val text = if (seconds > 0) seconds.toString() else ""
        switcher.setText(text)

        if (seconds == 0) {
            shakeBoard()
        }
    }

    private fun updateScore(score: Int, bestScore: Int) {
        binding.scoreText.text = getString(R.string.current_score_format, score)
        binding.bestScoreText.text = getString(R.string.high_score_format, bestScore)
    }

    private fun shakeBoard() {
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
                            .translationX(0f)
                            .duration = 50
                    }
            }
    }

    private fun navigateToGameOver(state: GameUiState.GameOver) {
        val dir = GameFragmentDirections.actionGameToGameOver(
            score = state.score,
            bestScore = state.bestScore,
            gameMode = state.gameMode
        )
        findNavController().navigate(dir)
    }

    private fun showRestartDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.WoodDialogTheme)
            .setTitle("Restart Game")
            .setMessage("Are you sure you want to restart the current game?")
            .setNegativeButton("NO") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("YES") { _, _ -> viewModel.restartGame() }
            .show()
    }

    private fun showExitDialog(score: Int) {
        MaterialAlertDialogBuilder(requireContext(), R.style.WoodDialogTheme)
            .setTitle("Exit Game")
            .setMessage("Are you sure you want to leave the current game?")
            .setNegativeButton("NO") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("YES") { _, _ ->
                viewModel.saveBestScoreIfNeeded(score)
                findNavController().navigateUp()
            }
            .show()
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
        viewModel.saveGame()
        musicManager.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        musicManager.stopBackgroundMusic()
    }
}