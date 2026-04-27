package dev.greecetripplanner.ui.screens.show

import androidx.compose.foundation.background
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
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.greecetripplanner.data.TripData
import dev.greecetripplanner.data.model.DayNarrative
import dev.greecetripplanner.data.model.TripDay
import dev.greecetripplanner.ui.TripViewModel
import dev.greecetripplanner.ui.components.DayTabs
import dev.greecetripplanner.ui.components.LinkChip
import dev.greecetripplanner.ui.components.TransitChip
import dev.greecetripplanner.util.driveHours
import dev.greecetripplanner.util.driveKm
import dev.greecetripplanner.util.formatHours
import dev.greecetripplanner.util.shareTrip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowScreen(
    viewModel: TripViewModel,
    onSwitchToPlan: () -> Unit,
    onSwitchToMap: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val activeTemplateKey by viewModel.activeTemplate.collectAsState()
    val darkModeOverride by viewModel.darkModeOverride.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val context = LocalContext.current

    // Lookup active template for narratives
    val template = activeTemplateKey?.let { key ->
        TripData.templates.find { it.key == key }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🇬🇷 Trip Itinerary") },
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
                    label = { Text("📋 Show") },
                )
                Spacer(Modifier.width(8.dp))
                FilterChip(
                    selected = false,
                    onClick = onSwitchToMap,
                    label = { Text("🗺️ Map") },
                )
            }

            // Stats bar
            if (stats.totalPois > 0) {
                StatsBar(
                    totalKm = stats.totalKm,
                    totalDays = stats.activeDays,
                    regions = stats.uniqueRegions,
                    pois = stats.totalPois,
                    fuelCost = stats.fuelCostEur,
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
                    val narrative = template?.dayNarratives?.get(day.dayIndex)
                    DayCard(
                        day = day,
                        dayIndex = day.dayIndex,
                        narrative = narrative,
                        prevRegion = uiState.days.getOrNull(day.dayIndex - 1)?.region,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsBar(
    totalKm: Int,
    totalDays: Int,
    regions: Int,
    pois: Int,
    fuelCost: Double,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        StatItem(value = "%,d".format(totalKm), label = "KM")
        StatItem(value = "$totalDays", label = "DAYS")
        StatItem(value = "$regions", label = "REGIONS")
        StatItem(value = "$pois", label = "POIs")
        if (fuelCost > 0) {
            StatItem(value = "€${"%.0f".format(fuelCost)}", label = "FUEL")
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}

@Composable
private fun DayCard(
    day: TripDay,
    dayIndex: Int,
    narrative: DayNarrative?,
    prevRegion: String?,
) {
    val uriHandler = LocalUriHandler.current
    val dayColor = TripData.dayColors.getOrNull(dayIndex)?.let {
        Color(it)
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
            // Drive banner (between days)
            if (prevRegion != null && day.region != null && prevRegion != day.region) {
                val driveH = driveHours(prevRegion, day.region)
                val km = driveKm(prevRegion, day.region)
                if (driveH > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("🚗", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "$km km · ~${formatHours(driveH)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

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

            // Narrative tagline
            narrative?.let { n ->
                Text(
                    text = n.tagline,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = n.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    modifier = Modifier.padding(top = 2.dp),
                )
                n.tip?.let { tip ->
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.tertiary,
                            fontStyle = FontStyle.Italic,
                        ),
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                Spacer(Modifier.height(6.dp))
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

            day.userNote?.let {
                Text(
                    text = "📝 $it",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        fontStyle = FontStyle.Italic,
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
                        val transitMin = day.region?.let { TripData.regionMap[it]?.transitMinutes } ?: 15
                        TransitChip(minutes = transitMin, isSameRegion = true)
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
                            poi.tip?.let { tip ->
                                Text(
                                    text = tip,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontStyle = FontStyle.Italic,
                                    ),
                                    modifier = Modifier.padding(top = 1.dp),
                                )
                            }
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
