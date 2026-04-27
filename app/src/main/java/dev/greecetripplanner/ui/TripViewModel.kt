package dev.greecetripplanner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}
