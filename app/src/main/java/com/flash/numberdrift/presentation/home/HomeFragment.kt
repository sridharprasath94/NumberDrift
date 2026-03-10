package com.flash.numberdrift.presentation.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.flash.numberdrift.R
import com.flash.numberdrift.databinding.FragmentHomeBinding
import dev.androidbroadcast.vbpd.viewBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding: FragmentHomeBinding by viewBinding(FragmentHomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            playButton.setOnClickListener {
                findNavController().navigate(R.id.action_start_to_game)
            }

            settingsButton.setOnClickListener {
                findNavController().navigate(R.id.action_start_to_settings)
            }
        }
    }

}