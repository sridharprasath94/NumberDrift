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
import com.flash.numberdrift.R
import com.flash.numberdrift.databinding.FragmentGameoverBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameOverFragment : Fragment(R.layout.fragment_gameover) {
    private val binding: FragmentGameoverBinding by viewBinding(FragmentGameoverBinding::bind)
    private val viewModel: GameOverViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            GameOverFragmentArgs.fromBundle(requireArguments()).score.let { score ->
                scoreText.text = getString(R.string.score_format, score)
            }

            playAgainButton.setOnClickListener {
                findNavController().navigate(
                    GameOverFragmentDirections.actionGameOverToGame(),
                )
            }

            homeButton.setOnClickListener {
                findNavController().navigate(GameOverFragmentDirections.actionGameOverToHome())
            }

            // Override system back press to go to Home instead of Game
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        findNavController().navigate(GameOverFragmentDirections.actionGameOverToHome())
                    }
                }
            )
        }

        observeViewModel()
    }


    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is GameOverUiState.Content -> {
                            binding.bestScoreText.text =
                                getString(R.string.best_score_format, state.bestScore)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}