package dev.greecetripplanner.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for persisting a day's plan.
 * Region + POI IDs are stored as strings (JSON-serialized list for poiIds).
 */
@Entity(tableName = "trip_days")
data class TripDayEntity(
    @PrimaryKey val dayIndex: Int,
    val region: String?,
    val poiIdsJson: String, // JSON array of POI ID strings
    val userNote: String? = null,
)
