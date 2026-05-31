package com.sanguo2.assistant.data.di

import android.content.Context
import com.sanguo2.assistant.data.repository.GameDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideGameDataRepository(@ApplicationContext context: Context): GameDataRepository {
        return GameDataRepository(context)
    }
}
