package com.example.app.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Events::class], version = 1)
abstract class CaiDatabase : RoomDatabase() {
    abstract val dao: EventDao

    companion object {
        @Volatile
        private var INSTANCE: CaiDatabase? = null

        fun getInstance(context: Context): CaiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CaiDatabase::class.java,
                    "CaiDatabase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

