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

    /** User-created custom POIs, stored in-memory per session. */
    private val _customPois = MutableStateFlow<List<Poi>>(emptyList())
    val customPois: StateFlow<List<Poi>> = _customPois.asStateFlow()

    fun setFilter(cat: String) { _activeFilter.value = cat }

    /** Create a custom POI for the given region and add it to the day. */
    fun addCustomPoi(dayIndex: Int, name: String, hours: Double, regionKey: String) {
        val id = "custom_${System.currentTimeMillis()}"
        val region = TripData.regionMap[regionKey]
        val poi = Poi(
            id = id,
            name = name,
            region = regionKey,
            hours = hours,
            categories = listOf("custom"),
            lat = region?.centerLat ?: 0.0,
            lng = region?.centerLng ?: 0.0,
            description = "Custom activity",
        )
        _customPois.value = _customPois.value + poi
        addPoi(dayIndex, id)
    }

    /** Resolve a POI by ID, checking custom POIs first then static data. */
    fun resolvePoi(id: String): Poi? =
        _customPois.value.find { it.id == id } ?: TripData.poiMap[id]

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

    /** Sort POIs on a day by nearest-neighbor from region center. */
    fun optimizeRoute(dayIndex: Int) {
        viewModelScope.launch {
            val days = repository.getDays()
            val day = days[dayIndex]
            if (day.poiIds.size < 2) return@launch
            val region = day.region?.let { TripData.regionMap[it] } ?: return@launch
            val pois = day.poiIds.mapNotNull { TripData.poiMap[it] }
            if (pois.size != day.poiIds.size) return@launch

            // Greedy nearest-neighbor starting from region center
            val sorted = mutableListOf<String>()
            val remaining = pois.toMutableList()
            var curLat = region.centerLat
            var curLng = region.centerLng
            while (remaining.isNotEmpty()) {
                val nearest = remaining.minBy { p ->
                    val dlat = p.lat - curLat
                    val dlng = p.lng - curLng
                    dlat * dlat + dlng * dlng
                }
                sorted.add(nearest.id)
                curLat = nearest.lat
                curLng = nearest.lng
                remaining.remove(nearest)
            }
            repository.updateDay(day.copy(poiIds = sorted))
        }
    }

    /** Swap a POI with its neighbor in the day's poi list. */
    fun reorderPoi(dayIndex: Int, poiId: String, direction: Int) {
        viewModelScope.launch {
            val days = repository.getDays()
            val day = days[dayIndex]
            val list = day.poiIds.toMutableList()
            val idx = list.indexOf(poiId)
            if (idx == -1) return@launch
            val newIdx = idx + direction
            if (newIdx < 0 || newIdx >= list.size) return@launch
            list[idx] = list[newIdx].also { list[newIdx] = list[idx] }
            repository.updateDay(day.copy(poiIds = list))
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
