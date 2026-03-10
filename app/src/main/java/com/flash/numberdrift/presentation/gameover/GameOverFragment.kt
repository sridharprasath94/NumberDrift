package com.flash.numberdrift.presentation.gameover

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.flash.numberdrift.R
import com.flash.numberdrift.databinding.FragmentGameoverBinding
import dev.androidbroadcast.vbpd.viewBinding

class GameOverFragment: Fragment(R.layout.fragment_gameover) {
    private val binding: FragmentGameoverBinding by viewBinding(FragmentGameoverBinding::bind)

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            with(binding) {

                playAgainButton.setOnClickListener {

                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.gameOverFragment, true)
                        .build()

                    findNavController().navigate(
                        R.id.action_gameOver_to_game,
                        null,
                        navOptions
                    )
                }

                homeButton.setOnClickListener {
                    findNavController().navigate(R.id.action_gameOver_to_home)
                }

            }
        }
}