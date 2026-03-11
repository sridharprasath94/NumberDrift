package com.flash.numberdrift.presentation.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.flash.numberdrift.domain.usecase.GetGameSettingsUseCase
import com.flash.numberdrift.domain.usecase.SetGameSettingsUseCase
import com.flash.numberdrift.presentation.shared.GameSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getGameSettingsUseCase: GetGameSettingsUseCase,
    private val setGameSettingsUseCase: SetGameSettingsUseCase
) : ViewModel() {

    private val _settings = MutableStateFlow<GameSettings?>(null)
    val settings: StateFlow<GameSettings?> = _settings

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _settings.value = getGameSettingsUseCase()
        }
    }

    fun updateSound(enabled: Boolean) {
        val current = _settings.value ?: return
        save(current.copy(sound = enabled))
    }

    fun updateVibration(enabled: Boolean) {
        val current = _settings.value ?: return
        save(current.copy(vibration = enabled))
    }

    fun updateDarkMode(enabled: Boolean) {
        val current = _settings.value ?: return
        if (enabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        save(current.copy(darkMode = enabled))
    }


    /// TODO : Replace with billing manager. This is just a placeholder for now.
    fun updateAdsStatus() {
        val current = _settings.value ?: return
        save(current.copy(adsRemoved = !current.adsRemoved))
    }

    private fun save(newSettings: GameSettings) {
        viewModelScope.launch {
            setGameSettingsUseCase(newSettings)
            _settings.value = newSettings
        }
    }
}