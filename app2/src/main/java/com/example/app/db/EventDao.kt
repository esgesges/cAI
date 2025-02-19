package com.example.app.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Upsert
    suspend fun upsertEvent(event: Events)

    @Delete
    suspend fun delEvent(event: Events): Int  // ✅ Correct return type

    @Query("SELECT * FROM Events")
    fun getEvents(): Flow<List<Events>>

    @Query("SELECT * FROM Events WHERE year = :year AND month = :month AND day = :day")
    fun getEventsByDate(year: Int, month: Int, day: Int): Flow<List<Events>>  // ✅ Correct Query
}