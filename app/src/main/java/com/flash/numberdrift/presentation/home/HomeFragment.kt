package com.flash.numberdrift.presentation.home

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.flash.numberdrift.R
import com.flash.numberdrift.databinding.FragmentHomeBinding
import com.flash.numberdrift.presentation.shared.GameMode
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding: FragmentHomeBinding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel: HomeViewModel by viewModels()

    private fun setupModeButtons(
        newGameButton: Button,
        continueButton: Button,
        mode: GameMode
    ) {
        newGameButton.setOnClickListener {
            viewModel.clearSavedGame(mode)
            findNavController().navigate(
                HomeFragmentDirections.actionHomeToGame(mode)
            )
        }

        continueButton.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeToGame(mode)
            )
        }
    }

    private fun renderModeState(
        bestScoreView: android.widget.TextView,
        savedScoreView: android.widget.TextView,
        continueButton: Button,
        bestScore: Int,
        savedScore: Int?
    ) {
        bestScoreView.text = getString(R.string.high_score_format, bestScore)

        if (savedScore != null) {
            continueButton.visibility = View.VISIBLE
            savedScoreView.visibility = View.VISIBLE
            savedScoreView.text = getString(R.string.current_score_format, savedScore)
            continueButton.text = getString(R.string.continue_game)
        } else {
            continueButton.visibility = View.GONE
            savedScoreView.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            setupModeButtons(classicNewGameButton, classicContinueButton, GameMode.CLASSIC)
            setupModeButtons(drift2NewGameButton, drift2ContinueButton, GameMode.DRIFT_2S)
            setupModeButtons(drift1NewGameButton, drift1ContinueButton, GameMode.DRIFT_1S)

            settingsButton.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeToSettings())
            }
        }
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshBestScores()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is HomeUiState.Content -> {

                            renderModeState(
                                bestScoreView = binding.classicBestScore,
                                savedScoreView = binding.classicSavedScore,
                                continueButton = binding.classicContinueButton,
                                bestScore = state.bestScoreClassicMode,
                                savedScore = state.savedClassicScore
                            )

                            renderModeState(
                                bestScoreView = binding.drift2BestScore,
                                savedScoreView = binding.drift2SavedScore,
                                continueButton = binding.drift2ContinueButton,
                                bestScore = state.bestScoreDrift2Mode,
                                savedScore = state.savedDrift2Score
                            )

                            renderModeState(
                                bestScoreView = binding.drift1BestScore,
                                savedScoreView = binding.drift1SavedScore,
                                continueButton = binding.drift1ContinueButton,
                                bestScore = state.bestScoreDrift1Mode,
                                savedScore = state.savedDrift1Score
                            )
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}