package com.andresilva.greecetripplanner.ui.screens.show

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.andresilva.greecetripplanner.data.TripData
import com.andresilva.greecetripplanner.data.model.TripDay
import com.andresilva.greecetripplanner.ui.TripViewModel
import com.andresilva.greecetripplanner.ui.components.DayTabs
import com.andresilva.greecetripplanner.ui.components.LinkChip
import com.andresilva.greecetripplanner.ui.components.TransitChip
import com.andresilva.greecetripplanner.util.formatHours
import com.andresilva.greecetripplanner.util.shareTrip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowScreen(
    viewModel: TripViewModel,
    onSwitchToPlan: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🇬🇷 Trip Itinerary") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                actions = {
                    IconButton(onClick = { shareTrip(context, uiState.days) }) {
                        Icon(Icons.Default.Share, "Share", tint = MaterialTheme.colorScheme.onPrimary)
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
                    selected = true,
                    onClick = {},
                    label = { Text("🗺️ Show") },
                )
            }

            // Day tabs
            DayTabs(
                days = uiState.days,
                activeDay = uiState.activeDay,
                onDaySelected = { viewModel.setActiveDay(it) },
            )

            // Day cards
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.days, key = { it.dayIndex }) { day ->
                    DayCard(day = day, dayIndex = day.dayIndex)
                }
            }
        }
    }
}

@Composable
private fun DayCard(day: TripDay, dayIndex: Int) {
    val uriHandler = LocalUriHandler.current
    val dayColor = TripData.dayColors.getOrNull(dayIndex)?.let {
        try { Color(android.graphics.Color.parseColor(it)) }
        catch (_: Exception) { MaterialTheme.colorScheme.primary }
    } ?: MaterialTheme.colorScheme.primary

    val regionName = day.region?.let { rk ->
        TripData.regions.find { it.key == rk }?.name
    } ?: "No region"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Day $dayIndex: ${TripDay.DATES[dayIndex]}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = dayColor,
                    ),
                )
                Text(
                    text = regionName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }

            day.note?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    modifier = Modifier.padding(top = 2.dp),
                )
            }

            Spacer(Modifier.height(8.dp))

            if (day.poiIds.isEmpty()) {
                Text(
                    text = "No activities planned",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            } else {
                day.poiIds.forEachIndexed { idx, poiId ->
                    val poi = TripData.poiMap[poiId] ?: return@forEachIndexed
                    val cat = TripData.categoryMap[poi.categories.firstOrNull() ?: ""]

                    if (idx > 0) {
                        TransitChip(minutes = 15, isSameRegion = true)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            text = "${cat?.icon ?: "📍"} ",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = poi.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                ),
                            )
                            Text(
                                text = poi.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 2.dp),
                            ) {
                                LinkChip(
                                    icon = Icons.Default.Map,
                                    label = "Map",
                                    onClick = { uriHandler.openUri(poi.mapsUrl) },
                                )
                                LinkChip(
                                    icon = Icons.Default.Search,
                                    label = "Info",
                                    onClick = { uriHandler.openUri(poi.searchUrl) },
                                )
                            }
                        }
                        Text(
                            text = formatHours(poi.hours),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = dayColor,
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                }
            }
        }
    }
}
