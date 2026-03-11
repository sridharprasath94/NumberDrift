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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            classicModeButton.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeToGame(GameMode.CLASSIC)
                )
            }

            drift2ModeButton.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeToGame(GameMode.DRIFT_2S)
                )
            }

            drift1ModeButton.setOnClickListener {
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
                            binding.classicHighScore.text =
                                getString(R.string.best_score_format, state.bestScoreClassicMode)
                            binding.drift2HighScore.text =
                                getString(R.string.best_score_format, state.bestScoreDrift2Mode)
                            binding.drift1HighScore.text =
                                getString(R.string.best_score_format, state.bestScoreDrift1Mode)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}