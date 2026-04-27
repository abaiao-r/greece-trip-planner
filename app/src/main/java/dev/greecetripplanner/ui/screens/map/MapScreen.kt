package dev.greecetripplanner.ui.screens.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.greecetripplanner.ui.TripViewModel
import dev.greecetripplanner.ui.components.DayTabs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: TripViewModel,
    onSwitchToPlan: () -> Unit,
    onSwitchToShow: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val darkModeOverride by viewModel.darkModeOverride.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🗺️ Trip Map") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                actions = {
                    IconButton(onClick = { viewModel.cycleDarkMode() }) {
                        val icon = when (darkModeOverride) {
                            null -> "🌗"
                            true -> "🌙"
                            false -> "☀️"
                        }
                        Text(icon, color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Mode toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilterChip(
                    selected = false,
                    onClick = onSwitchToPlan,
                    label = { Text("📝 Plan") },
                )
                Spacer(Modifier.width(8.dp))
                FilterChip(
                    selected = false,
                    onClick = onSwitchToShow,
                    label = { Text("📋 Show") },
                )
                Spacer(Modifier.width(8.dp))
                FilterChip(
                    selected = true,
                    onClick = {},
                    label = { Text("🗺️ Map") },
                )
            }

            // Day tabs — tap to zoom to a day, tap again to show all
            DayTabs(
                days = uiState.days,
                activeDay = uiState.activeDay,
                onDaySelected = { viewModel.setActiveDay(it) },
            )

            // Map
            MapComposable(
                days = uiState.days,
                activeDayIndex = null, // always show all days with full route
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
