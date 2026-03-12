package com.flash.numberdrift.presentation.gameover

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.flash.numberdrift.R
import com.flash.numberdrift.databinding.FragmentGameoverBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameOverFragment : Fragment(R.layout.fragment_gameover) {
    private val binding: FragmentGameoverBinding by viewBinding(FragmentGameoverBinding::bind)
    private val viewModel: GameOverViewModel by viewModels()
    private val args: GameOverFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupScore()
        setupButtons()
        setupBackHandler()

        observeViewModel()
    }

    private fun setupScore() {
        val score = args.score
        binding.scoreText.text = getString(R.string.current_score_format, score)
    }

    private fun setupButtons() {

        binding.playAgainButton.setOnClickListener {
            findNavController().navigate(
                GameOverFragmentDirections.actionGameOverToGame(args.gameMode)
            )
        }

        binding.homeButton.setOnClickListener {
            navigateHome()
        }
    }

    private fun setupBackHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateHome()
                }
            }
        )
    }

    private fun navigateHome() {
        findNavController().navigate(
            GameOverFragmentDirections.actionGameOverToHome()
        )
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is GameOverUiState.Content -> {
                            binding.bestScoreText.text =
                                getString(R.string.high_score_format, state.bestScore)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}