package com.andresilva.greecetripplanner.util

import com.andresilva.greecetripplanner.data.TripData
import com.andresilva.greecetripplanner.data.model.TripDay

/** Format hours as "2h" or "1.5h". */
fun formatHours(h: Double): String =
    if (h % 1.0 == 0.0) "${h.toInt()}h" else "${"%.1f".format(h)}h"

/** Format minutes. */
fun formatMinutes(m: Int): String = "${m}min"

/** Get drive hours between two regions. */
fun driveHours(from: String?, to: String?): Double {
    if (from == null || to == null || from == to) return 0.0
    return TripData.driveHours(from, to)
}

/** Get drive km between two regions. */
fun driveKm(from: String?, to: String?): Int {
    if (from == null || to == null || from == to) return 0
    return TripData.driveKm(from, to)
}

/** Calculate total activity hours for a day's selected POIs. */
fun activityHours(poiIds: List<String>): Double =
    poiIds.sumOf { TripData.poiMap[it]?.hours ?: 0.0 }

/** Estimate transit time within a region for a day. */
fun transitHours(poiIds: List<String>, regionKey: String?): Double {
    if (regionKey == null || poiIds.size < 2) return 0.0
    val region = TripData.regionMap[regionKey] ?: return 0.0
    return (poiIds.size - 1) * region.transitMinutes / 60.0
}

/** Available hours for a day. */
fun availableHours(dayIndex: Int): Double = TripDay.BASE_HOURS[dayIndex]

/** Budget status: ok, tight, or over. */
fun budgetStatus(used: Double, available: Double): String = when {
    used <= available * 0.85 -> "ok"
    used <= available -> "tight"
    else -> "over"
}
