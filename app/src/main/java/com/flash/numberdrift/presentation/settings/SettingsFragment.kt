package com.flash.numberdrift.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.compose.ui.tooling.preview.AndroidUiMode
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.flash.numberdrift.R
import com.flash.numberdrift.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val binding: FragmentSettingsBinding by viewBinding(FragmentSettingsBinding::bind)
    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.settings.collect { settings ->
                        settings?.let {
                            soundSwitch.isChecked = it.sound
                            vibrationSwitch.isChecked = it.vibration
                            darkModeSwitch.isChecked = it.darkMode

//                            if (it.adsRemoved) {
//                                removeAdsButton.text = "Ads Removed ✓"
//                            } else {
//                                removeAdsButton.text = "Remove Ads (€1.99)"
//                            }
                        }
                    }
                }
            }

            soundSwitch.setOnCheckedChangeListener { _, checked ->
                viewModel.updateSound(checked)
            }

            vibrationSwitch.setOnCheckedChangeListener { _, checked ->
                viewModel.updateVibration(checked)
            }

            darkModeSwitch.setOnCheckedChangeListener { _, checked ->
                viewModel.updateDarkMode(checked)
            }

            removeAdsButton.setOnClickListener {
                viewModel.updateAdsStatus()
            }
        }
    }
}