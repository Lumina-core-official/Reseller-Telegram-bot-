package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MessageEntity::class, DiagnosticLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CyberDatabase : RoomDatabase() {
    abstract fun cyberDao(): CyberDao

    companion object {
        @Volatile
        private var INSTANCE: CyberDatabase? = null

        fun getInstance(context: Context): CyberDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CyberDatabase::class.java,
                    "cyber_ops_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
