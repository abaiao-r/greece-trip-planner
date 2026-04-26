package com.andresilva.greecetripplanner.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TripDayEntity::class], version = 1, exportSchema = false)
abstract class TripDatabase : RoomDatabase() {
    abstract fun tripDayDao(): TripDayDao
}
