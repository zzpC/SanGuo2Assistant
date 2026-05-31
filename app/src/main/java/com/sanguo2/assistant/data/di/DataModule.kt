package com.sanguo2.assistant.data.di

import android.content.Context
import com.sanguo2.assistant.data.local.AppDatabase
import com.sanguo2.assistant.data.local.dao.ForceNoteDao
import com.sanguo2.assistant.data.local.dao.SoldierConfigDao
import com.sanguo2.assistant.data.repository.GameDataRepository
import com.sanguo2.assistant.data.repository.GarrisonNoteRepository
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

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideForceNoteDao(database: AppDatabase): ForceNoteDao {
        return database.forceNoteDao()
    }

    @Provides
    fun provideSoldierConfigDao(database: AppDatabase): SoldierConfigDao {
        return database.soldierConfigDao()
    }

    @Provides
    @Singleton
    fun provideGarrisonNoteRepository(
        forceNoteDao: ForceNoteDao,
        soldierConfigDao: SoldierConfigDao
    ): GarrisonNoteRepository {
        return GarrisonNoteRepository(forceNoteDao, soldierConfigDao)
    }
}
