package com.flash.numberdrift.framework.effects

import android.content.Context
import android.media.MediaPlayer
import com.flash.numberdrift.R
import com.flash.numberdrift.di.IoDispatcher
import com.flash.numberdrift.domain.usecase.GetGameSettingsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val getGameSettingsUseCase: GetGameSettingsUseCase,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(ioDispatcher)

    fun startBackgroundMusic() {
        if (mediaPlayer != null) return
        scope.launch {
            val settings = getGameSettingsUseCase()
            if (!settings.sound) return@launch

            mediaPlayer = MediaPlayer.create(context, R.raw.bg_music).apply {
                isLooping = true
                setVolume(0.05f, 0.05f)
                start()
            }
        }
    }

    fun stopBackgroundMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun resume() {
        mediaPlayer?.start()
    }
}