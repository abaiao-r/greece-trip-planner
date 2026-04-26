package com.andresilva.greecetripplanner.ui.screens.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.andresilva.greecetripplanner.data.TripData
import com.andresilva.greecetripplanner.data.model.TripDay
import com.andresilva.greecetripplanner.ui.TripViewModel
import com.andresilva.greecetripplanner.ui.components.BudgetBar
import com.andresilva.greecetripplanner.ui.components.DayTabs
import com.andresilva.greecetripplanner.ui.components.PoiCard
import com.andresilva.greecetripplanner.ui.components.RegionDropdown
import com.andresilva.greecetripplanner.ui.components.SelectedPoiItem
import com.andresilva.greecetripplanner.ui.components.TemplateBar
import com.andresilva.greecetripplanner.ui.components.TransitChip
import com.andresilva.greecetripplanner.util.shareTrip
import com.andresilva.greecetripplanner.util.driveHours

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlanScreen(
    viewModel: TripViewModel,
    onSwitchToShow: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🇬🇷 Greece Trip Planner") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                actions = {
                    IconButton(onClick = { shareTrip(context, uiState.days) }) {
                        Icon(Icons.Default.Share, "Share", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    IconButton(onClick = { viewModel.clearTrip() }) {
                        Icon(Icons.Default.Delete, "Clear", tint = MaterialTheme.colorScheme.onPrimary)
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
            // Templates
            TemplateBar(onTemplateSelected = { viewModel.applyTemplate(it.key) })
            Spacer(Modifier.height(4.dp))

            // Day tabs
            DayTabs(
                days = uiState.days,
                activeDay = uiState.activeDay,
                onDaySelected = { viewModel.setActiveDay(it) },
            )

            // Mode toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilterChip(
                    selected = true,
                    onClick = {},
                    label = { Text("📝 Plan") },
                )
                Spacer(Modifier.width(8.dp))
                FilterChip(
                    selected = false,
                    onClick = onSwitchToShow,
                    label = { Text("🗺️ Show") },
                )
            }

            // Active day content
            val day = uiState.days.getOrNull(uiState.activeDay) ?: return@Column
            val isLocked = uiState.activeDay == 0 || uiState.activeDay == TripDay.TOTAL_DAYS - 1

            // Day header
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(
                    text = "Day ${uiState.activeDay}: ${TripDay.DATES[uiState.activeDay]}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                day.note?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
                Spacer(Modifier.height(6.dp))

                // Region dropdown
                if (!isLocked) {
                    val selectedRegion = day.region?.let { rk ->
                        TripData.regions.find { it.key == rk }
                    }
                    RegionDropdown(
                        selectedRegion = selectedRegion,
                        onRegionSelected = { viewModel.setDayRegion(uiState.activeDay, it?.key) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                }

                // Budget bar
                val budget = viewModel.dayBudget(day)
                BudgetBar(budget = budget)
            }

            Spacer(Modifier.height(8.dp))

            // Drive-in chip
            if (uiState.activeDay > 0) {
                val prevDay = uiState.days.getOrNull(uiState.activeDay - 1)
                val prevRegion = prevDay?.region
                val curRegion = day.region
                if (prevRegion != null && curRegion != null && prevRegion != curRegion) {
                    val drive = driveHours(prevRegion, curRegion)
                    if (drive > 0) {
                        val driveMin = (drive * 60).toInt()
                        TransitChip(
                            minutes = driveMin,
                            isSameRegion = false,
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }

            // Main content: Selected items + available POIs
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // Selected POIs section
                if (day.poiIds.isNotEmpty()) {
                    item {
                        Text(
                            text = "Selected (${day.poiIds.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                    itemsIndexed(day.poiIds, key = { _, id -> "sel_$id" }) { idx, poiId ->
                        val poi = TripData.poiMap[poiId] ?: return@itemsIndexed
                        if (idx > 0) {
                            TransitChip(minutes = 15, isSameRegion = true)
                        }
                        SelectedPoiItem(
                            poi = poi,
                            index = idx,
                            onRemove = { viewModel.removePoi(poiId) },
                        )
                    }
                    item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }
                }

                // Category filter chips
                item {
                    CategoryFilterRow(
                        activeFilter = uiState.activeFilter,
                        onFilterChanged = { viewModel.setFilter(it) },
                    )
                }

                // Available POIs
                val available = viewModel.filteredAvailablePois(day)
                item {
                    Text(
                        text = "Available POIs (${available.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
                items(available, key = { "avail_${it.id}" }) { poi ->
                    val addedOnDay = viewModel.poiOnDay(poi.id)
                    val isAdded = addedOnDay != -1
                    val budget = viewModel.dayBudget(day)
                    PoiCard(
                        poi = poi,
                        isAdded = isAdded,
                        addedOnDay = if (isAdded) addedOnDay else null,
                        isOverBudget = budget.freeH < poi.hours,
                        onAdd = { viewModel.addPoi(poi.id) },
                        onMove = if (isAdded) {{ viewModel.movePoi(poi.id) }} else null,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryFilterRow(
    activeFilter: String,
    onFilterChanged: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FilterChip(
            selected = activeFilter == "all",
            onClick = { onFilterChanged("all") },
            label = { Text("All") },
        )
        TripData.categories.forEach { cat ->
            FilterChip(
                selected = activeFilter == cat.key,
                onClick = {
                    onFilterChanged(if (activeFilter == cat.key) "all" else cat.key)
                },
                label = { Text("${cat.icon} ${cat.label}", style = MaterialTheme.typography.labelSmall) },
            )
        }
    }
}
