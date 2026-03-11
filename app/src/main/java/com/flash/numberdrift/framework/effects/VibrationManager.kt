package com.flash.numberdrift.framework.effects

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.flash.numberdrift.di.IoDispatcher
import com.flash.numberdrift.domain.usecase.settings.GetGameSettingsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class VibrationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val getGameSettingsUseCase: GetGameSettingsUseCase,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    private val scope = CoroutineScope(ioDispatcher)

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.VIBRATE)
    fun vibrateShort() {
        scope.launch {
            val settings = getGameSettingsUseCase()
            if (!settings.vibration) return@launch

            val vibrator =
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    40,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
    }
}