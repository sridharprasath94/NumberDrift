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

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {
    private val binding: FragmentGameBinding by viewBinding(FragmentGameBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {

            // Temporary navigation for testing the navigation flow
            shuffleButton.setOnClickListener {
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
    }
}