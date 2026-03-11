package com.flash.numberdrift.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.flash.numberdrift.R
import com.flash.numberdrift.databinding.FragmentSettingsBinding
import dev.androidbroadcast.vbpd.viewBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val binding: FragmentSettingsBinding by viewBinding(FragmentSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {

            removeAdsButton.setOnClickListener {
                findNavController().navigate(
                    SettingsFragmentDirections.actionSettingsToHome()
                )
            }

        }
    }
}