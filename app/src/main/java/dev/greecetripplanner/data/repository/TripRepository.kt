package dev.greecetripplanner.data.repository

import dev.greecetripplanner.data.TripData
import dev.greecetripplanner.data.db.TripDayDao
import dev.greecetripplanner.data.db.TripDayEntity
import dev.greecetripplanner.data.model.TripDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepository @Inject constructor(
    private val dao: TripDayDao,
) {
    private val json = Json { ignoreUnknownKeys = true }

    /** Observe all 8 days as a flow. Fills missing days with defaults. */
    fun observeDays(): Flow<List<TripDay>> = dao.observeAll().map { entities ->
        val entityMap = entities.associateBy { it.dayIndex }
        (0 until TripDay.TOTAL_DAYS).map { i ->
            entityMap[i]?.toDomain(i) ?: defaultDay(i)
        }
    }

    suspend fun getDays(): List<TripDay> {
        val entities = dao.getAll()
        val entityMap = entities.associateBy { it.dayIndex }
        return (0 until TripDay.TOTAL_DAYS).map { i ->
            entityMap[i]?.toDomain(i) ?: defaultDay(i)
        }
    }

    suspend fun updateDay(day: TripDay) {
        dao.upsert(day.toEntity())
    }

    suspend fun saveAllDays(days: List<TripDay>) {
        dao.upsertAll(days.map { it.toEntity() })
    }

    suspend fun clearAll() {
        dao.deleteAll()
    }

    /** Apply a template: sets regions + POIs for all 8 days. */
    suspend fun applyTemplate(templateKey: String) {
        val template = TripData.templates.find { it.key == templateKey } ?: return
        val days = (0 until TripDay.TOTAL_DAYS).map { i ->
            TripDay(
                dayIndex = i,
                date = TripDay.DATES[i],
                region = template.regions.getOrNull(i),
                poiIds = template.dayPois[i] ?: emptyList(),
                note = TripDay.DAY_NOTES[i]
            )
        }
        dao.upsertAll(days.map { it.toEntity() })
    }

    // ── Mapping ──

    private fun TripDayEntity.toDomain(index: Int): TripDay {
        val ids: List<String> = try {
            json.decodeFromString(poiIdsJson)
        } catch (_: Exception) {
            emptyList()
        }
        return TripDay(
            dayIndex = index,
            date = TripDay.DATES[index],
            region = region,
            poiIds = ids,
            note = TripDay.DAY_NOTES[index],
            userNote = userNote,
        )
    }

    private fun TripDay.toEntity() = TripDayEntity(
        dayIndex = dayIndex,
        region = region,
        poiIdsJson = json.encodeToString(poiIds),
        userNote = userNote,
    )

    private fun defaultDay(index: Int) = TripDay(
        dayIndex = index,
        date = TripDay.DATES[index],
        region = if (index == 0 || index == 7) "athens" else null,
        poiIds = emptyList(),
        note = TripDay.DAY_NOTES[index]
    )
}
