package com.flash.numberdrift.domain.usecase.score

import com.flash.numberdrift.data.repository.FakePreferenceRepositoryImpl
import com.flash.numberdrift.presentation.shared.GameMode
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SaveBestScoreUseCaseTest {

    private lateinit var fakeRepository: FakePreferenceRepositoryImpl
    private lateinit var useCase: SaveBestScoreUseCase

    @Before
    fun setup() {
        fakeRepository = FakePreferenceRepositoryImpl()
        useCase = SaveBestScoreUseCase(fakeRepository)
    }

    @Test
    fun `saves new best score when score is higher`() = runBlocking {

        fakeRepository.saveBestScore(100, GameMode.CLASSIC)

        useCase(score = 150, mode = GameMode.CLASSIC)

        val result = fakeRepository.getBestScore(GameMode.CLASSIC)

        assertEquals(150, result)
    }

    @Test
    fun `does not update best score when score is lower`() = runBlocking {

        fakeRepository.saveBestScore(200, GameMode.CLASSIC)

        useCase(score = 150, mode = GameMode.CLASSIC)

        val result = fakeRepository.getBestScore(GameMode.CLASSIC)

        assertEquals(200, result)
    }

    @Test
    fun `does not update best score when score is equal`() = runBlocking {

        fakeRepository.saveBestScore(200, GameMode.CLASSIC)

        useCase(score = 200, mode = GameMode.CLASSIC)

        val result = fakeRepository.getBestScore(GameMode.CLASSIC)

        assertEquals(200, result)
    }
}