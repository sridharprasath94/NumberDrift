package com.flash.numberdrift.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flash.numberdrift.R
import com.flash.numberdrift.databinding.FragmentSettingsBinding
import com.flash.numberdrift.presentation.shared.GameSettings
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val binding: FragmentSettingsBinding by viewBinding(FragmentSettingsBinding::bind)
    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeSettings()
        setupListeners()
    }

    private fun observeSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.settings.collect { settings ->
                    settings?.let { renderSettings(it) }
                }
            }
        }
    }

    private fun renderSettings(settings: GameSettings) {
        binding.soundSwitch.isChecked = settings.soundEffects
        binding.musicSwitch.isChecked = settings.music
        binding.vibrationSwitch.isChecked = settings.vibration
        binding.darkModeSwitch.isChecked = settings.darkMode

        // TODO: Implement in-app purchase and update button text based on purchase status
//        if (settings.adsRemoved) {
//            binding.removeAdsButton.text = "Ads Removed ✓"
//        } else {
//            binding.removeAdsButton.text = "Remove Ads (€1.99)"
//        }
    }

    private fun setupListeners() {

        binding.soundSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.updateSoundEffects(checked)
        }

        binding.musicSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.updateMusic(checked)
        }

        binding.vibrationSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.updateVibration(checked)
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.updateDarkMode(checked)
        }

        binding.removeAdsButton.setOnClickListener {
            viewModel.updateAdsStatus()
        }
    }
}