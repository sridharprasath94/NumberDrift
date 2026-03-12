package com.flash.numberdrift.domain.usecase.settings

import com.flash.numberdrift.data.repository.FakePreferenceRepositoryImpl
import com.flash.numberdrift.presentation.shared.GameSettings
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SetGameSettingsUseCaseTest {

    private lateinit var fakeRepository: FakePreferenceRepositoryImpl
    private lateinit var useCase: SetGameSettingsUseCase

    @Before
    fun setup() {
        fakeRepository = FakePreferenceRepositoryImpl()
        useCase = SetGameSettingsUseCase(fakeRepository)
    }

    @Test
    fun `saves all settings into repository`() = runBlocking {

        val settings = GameSettings(
            soundEffects = false,
            music = false,
            vibration = false,
            darkMode = true,
            adsRemoved = true
        )

        useCase(settings)

        assertEquals(false, fakeRepository.isSoundEffectsEnabled())
        assertEquals(false, fakeRepository.isMusicEnabled())
        assertEquals(false, fakeRepository.isVibrationEnabled())
        assertEquals(true, fakeRepository.isDarkModeEnabled())
        assertEquals(true, fakeRepository.isAdsRemoved())
    }

    @Test
    fun `returns the same settings object after saving`() = runBlocking {

        val settings = GameSettings(
            soundEffects = true,
            music = true,
            vibration = true,
            darkMode = false,
            adsRemoved = false
        )

        val result = useCase(settings)

        assertEquals(settings, result)
    }
}