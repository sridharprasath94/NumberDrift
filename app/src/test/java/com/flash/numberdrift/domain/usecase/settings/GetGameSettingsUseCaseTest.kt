package com.flash.numberdrift.domain.usecase.settings

import com.flash.numberdrift.data.repository.FakePreferenceRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetGameSettingsUseCaseTest {

    private lateinit var fakeRepository: FakePreferenceRepositoryImpl
    private lateinit var useCase: GetGameSettingsUseCase

    @Before
    fun setup() {
        fakeRepository = FakePreferenceRepositoryImpl()
        useCase = GetGameSettingsUseCase(fakeRepository)
    }

    @Test
    fun `returns default settings`() = runBlocking {

        val result = useCase()

        assertEquals(true, result.soundEffects)
        assertEquals(true, result.music)
        assertEquals(true, result.vibration)
        assertEquals(false, result.darkMode)
        assertEquals(false, result.adsRemoved)
    }

    @Test
    fun `returns updated settings from repository`() = runBlocking {

        fakeRepository.setSoundEffectsEnabled(false)
        fakeRepository.setMusicEnabled(false)
        fakeRepository.setVibrationEnabled(false)
        fakeRepository.setDarkModeEnabled(true)
        fakeRepository.setAdsRemoved(true)

        val result = useCase()

        assertEquals(false, result.soundEffects)
        assertEquals(false, result.music)
        assertEquals(false, result.vibration)
        assertEquals(true, result.darkMode)
        assertEquals(true, result.adsRemoved)
    }
}