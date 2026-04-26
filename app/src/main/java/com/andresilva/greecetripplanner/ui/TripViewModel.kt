package com.andresilva.greecetripplanner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andresilva.greecetripplanner.data.TripData
import com.andresilva.greecetripplanner.data.model.Poi
import com.andresilva.greecetripplanner.data.model.TripDay
import com.andresilva.greecetripplanner.data.repository.TripRepository
import com.andresilva.greecetripplanner.util.activityHours
import com.andresilva.greecetripplanner.util.availableHours
import com.andresilva.greecetripplanner.util.driveHours
import com.andresilva.greecetripplanner.util.transitHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TripUiState(
    val days: List<TripDay> = (0 until TripDay.TOTAL_DAYS).map { i ->
        TripDay(
            dayIndex = i,
            date = TripDay.DATES[i],
            region = if (i == 0 || i == 7) "athens" else null,
            note = TripDay.DAY_NOTES[i]
        )
    },
    val activeDay: Int = 0,
    val mode: AppMode = AppMode.PLAN,
    val activeFilter: String = "all",
)

enum class AppMode { PLAN, SHOW }

@HiltViewModel
class TripViewModel @Inject constructor(
    private val repository: TripRepository,
) : ViewModel() {

    private val _activeDay = MutableStateFlow(0)
    val activeDay: StateFlow<Int> = _activeDay.asStateFlow()

    private val _mode = MutableStateFlow(AppMode.PLAN)
    val mode: StateFlow<AppMode> = _mode.asStateFlow()

    private val _activeFilter = MutableStateFlow("all")
    val activeFilter: StateFlow<String> = _activeFilter.asStateFlow()

    val uiState: StateFlow<TripUiState> = combine(
        repository.observeDays(),
        _activeDay,
        _mode,
        _activeFilter,
    ) { days, day, mode, filter ->
        TripUiState(days = days, activeDay = day, mode = mode, activeFilter = filter)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TripUiState())

    fun setActiveDay(day: Int) { _activeDay.value = day }
    fun setMode(mode: AppMode) { _mode.value = mode }
    fun setFilter(cat: String) { _activeFilter.value = cat }

    // ── Region management ──

    fun setDayRegion(dayIndex: Int, regionKey: String?) {
        viewModelScope.launch {
            val current = repository.getDays()
            val day = current[dayIndex]
            // When changing region, clear POIs from old region
            val newPois = if (day.region != regionKey) emptyList() else day.poiIds
            repository.updateDay(day.copy(region = regionKey, poiIds = newPois))
        }
    }

    // ── POI management ──

    fun addPoi(poiId: String) {
        viewModelScope.launch {
            val days = repository.getDays()
            val d = _activeDay.value
            val day = days[d]
            if (poiId in day.poiIds) return@launch
            repository.updateDay(day.copy(poiIds = day.poiIds + poiId))
        }
    }

    fun removePoi(poiId: String) {
        viewModelScope.launch {
            val days = repository.getDays()
            val d = _activeDay.value
            val day = days[d]
            repository.updateDay(day.copy(poiIds = day.poiIds - poiId))
            _activeFilter.value = "all"
        }
    }

    fun movePoi(poiId: String) {
        viewModelScope.launch {
            val days = repository.getDays()
            val targetDay = _activeDay.value
            // Find which day currently has this POI
            val sourceDay = days.indexOfFirst { poiId in it.poiIds }
            if (sourceDay == -1 || sourceDay == targetDay) return@launch
            // Remove from source
            repository.updateDay(days[sourceDay].copy(poiIds = days[sourceDay].poiIds - poiId))
            // Add to target
            val target = days[targetDay]
            repository.updateDay(target.copy(poiIds = target.poiIds + poiId))
        }
    }

    /** Find which day has a given POI, or -1. */
    fun poiOnDay(poiId: String): Int {
        val days = uiState.value.days
        return days.indexOfFirst { poiId in it.poiIds }
    }

    fun isPoiAnywhere(poiId: String): Boolean = poiOnDay(poiId) != -1

    // ── Templates ──

    fun applyTemplate(key: String) {
        viewModelScope.launch {
            repository.applyTemplate(key)
            _activeDay.value = 0
            _activeFilter.value = "all"
        }
    }

    fun clearTrip() {
        viewModelScope.launch {
            repository.clearAll()
            _activeDay.value = 0
        }
    }

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

    fun dayBudget(day: TripDay): DayBudget {
        val act = activityHours(day.poiIds)
        val trans = transitHours(day.poiIds, day.region)
        val avail = availableHours(day.dayIndex)
        val prevRegion = uiState.value.days.getOrNull(day.dayIndex - 1)?.region
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
