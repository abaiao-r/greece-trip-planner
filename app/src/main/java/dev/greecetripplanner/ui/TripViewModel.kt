package dev.greecetripplanner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.greecetripplanner.data.TripData
import dev.greecetripplanner.data.model.TripDay
import dev.greecetripplanner.data.repository.TripRepository
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
)

data class TripStats(
    val totalKm: Int = 0,
    val totalPois: Int = 0,
    val uniqueRegions: Int = 0,
    val activeDays: Int = 0,
    val fuelCostEur: Double = 0.0,
) {
    companion object {
        const val FUEL_CONSUMPTION_L_PER_100KM = 7.0
        const val FUEL_PRICE_EUR_PER_L = 1.85
    }
}

/** Shared trip state used by all screens: days, active day, dark mode, active template. */
@HiltViewModel
class TripViewModel @Inject constructor(
    private val repository: TripRepository,
) : ViewModel() {

    private val _activeDay = MutableStateFlow(0)

    private val _activeTemplate = MutableStateFlow<String?>(null)
    val activeTemplate: StateFlow<String?> = _activeTemplate.asStateFlow()

    private val _darkModeOverride = MutableStateFlow<Boolean?>(null)
    val darkModeOverride: StateFlow<Boolean?> = _darkModeOverride.asStateFlow()

    val uiState: StateFlow<TripUiState> = combine(
        repository.observeDays(),
        _activeDay,
    ) { days, day ->
        TripUiState(days = days, activeDay = day)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TripUiState())

    val stats: StateFlow<TripStats> = repository.observeDays().combine(_activeDay) { days, _ ->
        val totalKm = days.zipWithNext().sumOf { (prev, cur) ->
            val a = prev.region; val b = cur.region
            if (a != null && b != null && a != b) TripData.driveKm(a, b) else 0
        }
        val fuelLitres = totalKm * TripStats.FUEL_CONSUMPTION_L_PER_100KM / 100.0
        TripStats(
            totalKm = totalKm,
            totalPois = days.sumOf { it.poiIds.size },
            uniqueRegions = days.mapNotNull { it.region }.distinct().size,
            activeDays = days.count { it.poiIds.isNotEmpty() },
            fuelCostEur = fuelLitres * TripStats.FUEL_PRICE_EUR_PER_L,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TripStats())

    fun setActiveDay(day: Int) { _activeDay.value = day }

    fun cycleDarkMode() {
        _darkModeOverride.value = when (_darkModeOverride.value) {
            null -> true
            true -> false
            false -> null
        }
    }

    fun applyTemplate(key: String) {
        viewModelScope.launch {
            repository.applyTemplate(key)
            _activeDay.value = 0
            _activeTemplate.value = key
        }
    }

    fun clearTrip() {
        viewModelScope.launch {
            repository.clearAll()
            _activeDay.value = 0
            _activeTemplate.value = null
        }
    }

    fun updateUserNote(dayIndex: Int, note: String) {
        viewModelScope.launch {
            val days = repository.getDays()
            val day = days[dayIndex]
            repository.updateDay(day.copy(userNote = note.ifBlank { null }))
        }
    }
}
