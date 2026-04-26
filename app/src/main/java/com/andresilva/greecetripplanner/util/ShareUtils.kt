package com.andresilva.greecetripplanner.util

import android.content.Context
import android.content.Intent
import com.andresilva.greecetripplanner.data.TripData
import com.andresilva.greecetripplanner.data.model.TripDay

/** Build a shareable text summary of the entire trip. */
fun buildShareText(days: List<TripDay>): String {
    val sb = StringBuilder()
    sb.appendLine("🇬🇷 Greece Road Trip — Jun 14–21, 2026")
    sb.appendLine("Athens round trip · Rental car")
    sb.appendLine("═".repeat(40))

    for (day in days) {
        val regionName = day.region?.let { TripData.regionMap[it]?.name } ?: "—"
        sb.appendLine()
        sb.appendLine("Day ${day.dayIndex} · ${day.date} · $regionName")

        if (day.note != null) {
            sb.appendLine("  📝 ${day.note}")
        }

        if (day.dayIndex > 0) {
            val prevRegion = days.getOrNull(day.dayIndex - 1)?.region
            val dh = driveHours(prevRegion, day.region)
            if (dh > 0) {
                sb.appendLine("  🚗 Drive: ~${formatHours(dh)}")
            }
        }

        if (day.poiIds.isEmpty()) {
            sb.appendLine("  (no stops planned)")
        } else {
            day.poiIds.forEachIndexed { i, id ->
                val poi = TripData.poiMap[id]
                if (poi != null) {
                    val cat = TripData.categoryMap[poi.categories.first()]
                    sb.appendLine("  ${i + 1}. ${cat?.icon ?: "📍"} ${poi.name} (${formatHours(poi.hours)})")
                }
            }
            val act = activityHours(day.poiIds)
            val tr = transitHours(day.poiIds, day.region)
            sb.appendLine("  ⏱ Total: ${formatHours(act)} activities + ${formatHours(tr)} transit")
        }
    }

    sb.appendLine()
    sb.appendLine("Built with Greece Trip Planner 🇬🇷")
    return sb.toString()
}

/** Share the trip via Android share sheet. */
fun shareTrip(context: Context, days: List<TripDay>) {
    val text = buildShareText(days)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Greece Road Trip — Jun 14–21, 2026")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share trip"))
}
