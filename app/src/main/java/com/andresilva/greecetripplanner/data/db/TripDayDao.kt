package com.andresilva.greecetripplanner.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDayDao {
    @Query("SELECT * FROM trip_days ORDER BY dayIndex ASC")
    fun observeAll(): Flow<List<TripDayEntity>>

    @Query("SELECT * FROM trip_days ORDER BY dayIndex ASC")
    suspend fun getAll(): List<TripDayEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(day: TripDayEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(days: List<TripDayEntity>)

    @Query("DELETE FROM trip_days")
    suspend fun deleteAll()
}
