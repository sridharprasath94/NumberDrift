package com.flash.numberdrift.di

import android.content.Context
import android.content.SharedPreferences
import com.flash.numberdrift.data.repository.PreferenceRepositoryImpl
import com.flash.numberdrift.domain.repository.PreferenceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            "number_drift_prefs",
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun providePreferenceRepository(
        impl: PreferenceRepositoryImpl
    ): PreferenceRepository = impl
}