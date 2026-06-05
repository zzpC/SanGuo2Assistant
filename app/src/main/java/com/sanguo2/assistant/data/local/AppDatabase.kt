package com.sanguo2.assistant.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sanguo2.assistant.data.local.dao.ForceNoteDao
import com.sanguo2.assistant.data.local.dao.SoldierConfigDao
import com.sanguo2.assistant.data.local.entity.ForceNote
import com.sanguo2.assistant.data.local.entity.SoldierConfig

@Database(
    entities = [ForceNote::class, SoldierConfig::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun forceNoteDao(): ForceNoteDao
    abstract fun soldierConfigDao(): SoldierConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sanguo2_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
