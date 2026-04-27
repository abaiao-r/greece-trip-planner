package dev.greecetripplanner.data.model

import kotlinx.serialization.Serializable

/**
 * The user's trip state — persisted to Room.
 * Each day has an optional region and a list of POI IDs.
 */
@Serializable
data class TripDay(
    val dayIndex: Int,
    val date: String,
    val region: String? = null,
    val poiIds: List<String> = emptyList(),
    val note: String? = null
) {
    companion object {
        const val TOTAL_DAYS = 8
        val DATES = listOf(
            "Sat Jun 14", "Sun Jun 15", "Mon Jun 16", "Tue Jun 17",
            "Wed Jun 18", "Thu Jun 19", "Fri Jun 20", "Sat Jun 21"
        )
        val BASE_HOURS = listOf(3.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 4.0)
        val DAY_NOTES = listOf(
            "Night arrival ~8 PM. Dinner and first walk.",
            null, null, null, null, null, null,
            "Morning free. Airport by 1 PM."
        )
    }
}
