package dev.greecetripplanner.ui.screens.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.greecetripplanner.data.TripData
import dev.greecetripplanner.data.model.Poi
import dev.greecetripplanner.data.model.TripDay
import dev.greecetripplanner.data.repository.TripRepository
import dev.greecetripplanner.util.activityHours
import dev.greecetripplanner.util.availableHours
import dev.greecetripplanner.util.driveHours
import dev.greecetripplanner.util.transitHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Plan-screen specific ViewModel: POI editing, region management, category filter, budget. */
@HiltViewModel
class PlanViewModel @Inject constructor(
    private val repository: TripRepository,
) : ViewModel() {

    private val _activeFilter = MutableStateFlow("all")
    val activeFilter: StateFlow<String> = _activeFilter.asStateFlow()

    fun setFilter(cat: String) { _activeFilter.value = cat }

    // ── Region management ──

    fun setDayRegion(dayIndex: Int, regionKey: String?) {
        viewModelScope.launch {
            val current = repository.getDays()
            val day = current[dayIndex]
            val newPois = if (day.region != regionKey) emptyList() else day.poiIds
            repository.updateDay(day.copy(region = regionKey, poiIds = newPois))
        }
    }

    // ── POI management ──

    fun addPoi(dayIndex: Int, poiId: String) {
        viewModelScope.launch {
            val days = repository.getDays()
            val day = days[dayIndex]
            if (poiId in day.poiIds) return@launch
            repository.updateDay(day.copy(poiIds = day.poiIds + poiId))
        }
    }

    fun removePoi(dayIndex: Int, poiId: String) {
        viewModelScope.launch {
            val days = repository.getDays()
            val day = days[dayIndex]
            repository.updateDay(day.copy(poiIds = day.poiIds - poiId))
            _activeFilter.value = "all"
        }
    }

    fun movePoi(targetDay: Int, poiId: String) {
        viewModelScope.launch {
            val days = repository.getDays()
            val sourceDay = days.indexOfFirst { poiId in it.poiIds }
            if (sourceDay == -1 || sourceDay == targetDay) return@launch
            repository.updateDay(days[sourceDay].copy(poiIds = days[sourceDay].poiIds - poiId))
            val target = days[targetDay]
            repository.updateDay(target.copy(poiIds = target.poiIds + poiId))
        }
    }

    fun poiOnDay(days: List<TripDay>, poiId: String): Int =
        days.indexOfFirst { poiId in it.poiIds }

    fun isPoiAnywhere(days: List<TripDay>, poiId: String): Boolean =
        poiOnDay(days, poiId) != -1

    // ── Computed helpers ──

    fun poisForRegion(regionKey: String?): List<Poi> {
        if (regionKey == null) return emptyList()
        return TripData.pois.filter { it.region == regionKey }
    }

    fun filteredAvailablePois(day: TripDay): List<Poi> {
        if (day.region == null) return emptyList()
        val allRegionPois = poisForRegion(day.region)
        val filter = _activeFilter.value
        return allRegionPois
            .filter { it.id !in day.poiIds }
            .filter { filter == "all" || filter in it.categories }
    }

    fun dayBudget(day: TripDay, prevRegion: String?): DayBudget {
        val act = activityHours(day.poiIds)
        val trans = transitHours(day.poiIds, day.region)
        val avail = availableHours(day.dayIndex)
        val drive = driveHours(prevRegion, day.region)
        val total = act + trans
        val free = avail - total
        return DayBudget(act, trans, drive, avail, total, free)
    }
}

data class DayBudget(
    val activityH: Double,
    val transitH: Double,
    val driveH: Double,
    val availableH: Double,
    val totalUsed: Double,
    val freeH: Double,
) {
    val status: String
        get() = when {
            totalUsed <= availableH * 0.85 -> "ok"
            totalUsed <= availableH -> "tight"
            else -> "over"
        }
    val percentage: Float
        get() = if (availableH > 0) (totalUsed / availableH).toFloat().coerceIn(0f, 1.2f) else 0f
}
