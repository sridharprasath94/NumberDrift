package com.flash.numberdrift.presentation.home

import android.os.Bundle
import android.view.View
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
    private var latestState: HomeUiState.Content? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {

            classicNewGameButton.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeToGame(GameMode.CLASSIC)
                )
            }

            classicContinueButton.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeToGame(GameMode.CLASSIC)
                )
            }

            drift2NewGameButton.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeToGame(GameMode.DRIFT_2S)
                )
            }

            drift2ContinueButton.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeToGame(GameMode.DRIFT_2S)
                )
            }

            drift1NewGameButton.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeToGame(GameMode.DRIFT_1S)
                )
            }

            drift1ContinueButton.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeToGame(GameMode.DRIFT_1S)
                )
            }

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

                            latestState = state

                            binding.classicBestScore.text =
                                getString(R.string.best_score_format, state.bestScoreClassicMode)

                            if (state.savedClassicScore != null) {
                                binding.classicContinueButton.visibility = View.VISIBLE
                                binding.classicSavedScore.visibility = View.VISIBLE
                                binding.classicSavedScore.text =
                                    getString(R.string.score_format, state.savedClassicScore)
                                binding.classicContinueButton.text = getString(R.string.continue_game)
                            } else {
                                binding.classicContinueButton.visibility = View.GONE
                                binding.classicSavedScore.visibility = View.GONE
                            }

                            binding.drift2BestScore.text =
                                getString(R.string.best_score_format, state.bestScoreDrift2Mode)

                            if (state.savedDrift2Score != null) {
                                binding.drift2ContinueButton.visibility = View.VISIBLE
                                binding.drift2SavedScore.visibility = View.VISIBLE
                                binding.drift2SavedScore.text =
                                    getString(R.string.score_format, state.savedDrift2Score)
                                binding.drift2ContinueButton.text = getString(R.string.continue_game)
                            } else {
                                binding.drift2ContinueButton.visibility = View.GONE
                                binding.drift2SavedScore.visibility = View.GONE
                            }

                            binding.drift1BestScore.text =
                                getString(R.string.best_score_format, state.bestScoreDrift1Mode)

                            if (state.savedDrift1Score != null) {
                                binding.drift1ContinueButton.visibility = View.VISIBLE
                                binding.drift1SavedScore.visibility = View.VISIBLE
                                binding.drift1SavedScore.text =
                                    getString(R.string.score_format, state.savedDrift1Score)
                                binding.drift1ContinueButton.text = getString(R.string.continue_game)
                            } else {
                                binding.drift1ContinueButton.visibility = View.GONE
                                binding.drift1SavedScore.visibility = View.GONE
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}