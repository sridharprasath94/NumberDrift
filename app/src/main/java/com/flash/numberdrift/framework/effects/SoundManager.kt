package com.flash.numberdrift.framework.effects

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.flash.numberdrift.R
import com.flash.numberdrift.domain.usecase.GetGameSettingsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.flash.numberdrift.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher

class SoundManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val getGameSettingsUseCase: GetGameSettingsUseCase,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    private val soundPool: SoundPool
    private val moveSoundId: Int
    private val mergeSoundId: Int
    private val driftSoundId: Int
    private val gameOverSoundId: Int
    private val scope = CoroutineScope(ioDispatcher)

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()

        moveSoundId = soundPool.load(context, R.raw.move, 1)
        mergeSoundId = soundPool.load(context, R.raw.merge, 1)
        driftSoundId = soundPool.load(context, R.raw.drift, 1)
        gameOverSoundId = soundPool.load(context, R.raw.game_over, 1)
    }

    fun playMoveSound() {
        scope.launch {
            val settings = getGameSettingsUseCase()
            if (!settings.sound) return@launch
            soundPool.play(moveSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun playMergeSound() {
        scope.launch {
            val settings = getGameSettingsUseCase()
            if (!settings.sound) return@launch
            soundPool.play(mergeSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun playDriftSound() {
        scope.launch {
            val settings = getGameSettingsUseCase()
            if (!settings.sound) return@launch
            soundPool.play(driftSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun playGameOverSound() {
        scope.launch {
            val settings = getGameSettingsUseCase()
            if (!settings.sound) return@launch
            soundPool.play(gameOverSoundId, 1f, 1f, 1, 0, 1f)
        }
    }
}