package com.flash.numberdrift.domain.usecase.score

import com.flash.numberdrift.data.repository.FakePreferenceRepositoryImpl
import com.flash.numberdrift.domain.repository.PreferenceRepository
import com.flash.numberdrift.presentation.shared.GameMode
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetBestScoreUseCaseTest {

    private lateinit var fakeRepository: PreferenceRepository
    private lateinit var useCase: GetBestScoreUseCase

    @Before
    fun setup() {
        fakeRepository = FakePreferenceRepositoryImpl()
        useCase = GetBestScoreUseCase(fakeRepository)
    }

    @Test
    fun `returns best score from repository`() = runBlocking {

        fakeRepository.saveBestScore(120, GameMode.CLASSIC)

        val result = useCase(GameMode.CLASSIC)

        assertEquals(120, result)
    }

    @Test
    fun `returns different scores for different modes`() = runBlocking {

        fakeRepository.saveBestScore(100, GameMode.CLASSIC)
        fakeRepository.saveBestScore(200, GameMode.DRIFT_2S)

        val classicScore = useCase(GameMode.CLASSIC)
        val driftScore = useCase(GameMode.DRIFT_2S)

        assertEquals(100, classicScore)
        assertEquals(200, driftScore)
    }

}