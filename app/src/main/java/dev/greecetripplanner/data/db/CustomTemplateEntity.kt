package dev.greecetripplanner.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_templates")
data class CustomTemplateEntity(
    @PrimaryKey val key: String,
    val name: String,
    val icon: String,
    val description: String,
    /** JSON-encoded List<String?> — one region key per day (8 entries) */
    val regionsJson: String,
    /** JSON-encoded Map<Int, List<String>> — day index to POI IDs */
    val dayPoisJson: String,
)
