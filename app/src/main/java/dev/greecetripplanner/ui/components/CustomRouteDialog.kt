package dev.greecetripplanner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.greecetripplanner.data.TripData
import dev.greecetripplanner.data.model.TripDay

private val ICON_OPTIONS = listOf("🗺️", "🏖️", "⛰️", "🏛️", "🍽️", "🌊", "🌿", "🏰", "✨", "🚗")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRouteDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, icon: String, regions: List<String>) -> Unit,
) {
    val name = remember { mutableStateOf("") }
    val selectedIcon = remember { mutableStateOf(ICON_OPTIONS.first()) }
    val dayRegions = remember {
        mutableStateListOf(*Array(TripDay.TOTAL_DAYS) { i ->
            if (i == 0 || i == TripDay.TOTAL_DAYS - 1) "athens" else ""
        })
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Custom Route") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Route name
                OutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Route name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                // Icon picker
                Text("Icon", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ICON_OPTIONS.forEach { icon ->
                        TextButton(onClick = { selectedIcon.value = icon }) {
                            Text(
                                text = icon,
                                style = if (icon == selectedIcon.value)
                                    MaterialTheme.typography.titleLarge
                                else MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))
                Text("Region per day", style = MaterialTheme.typography.labelMedium)

                // Per-day region selectors
                (0 until TripDay.TOTAL_DAYS).forEach { dayIdx ->
                    val isLocked = dayIdx == 0 || dayIdx == TripDay.TOTAL_DAYS - 1
                    DayRegionPicker(
                        dayIndex = dayIdx,
                        selectedKey = dayRegions[dayIdx],
                        isLocked = isLocked,
                        onRegionChanged = { dayRegions[dayIdx] = it },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name.value.trim(), selectedIcon.value, dayRegions.toList()) },
                enabled = name.value.isNotBlank(),
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayRegionPicker(
    dayIndex: Int,
    selectedKey: String,
    isLocked: Boolean,
    onRegionChanged: (String) -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }
    val regionName = TripData.regionMap[selectedKey]?.name ?: "— none —"

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(
            text = "Day $dayIndex",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(48.dp),
        )
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { if (!isLocked) expanded.value = it },
            modifier = Modifier.weight(1f),
        ) {
            OutlinedTextField(
                value = regionName,
                onValueChange = {},
                readOnly = true,
                enabled = !isLocked,
                textStyle = MaterialTheme.typography.bodySmall,
                trailingIcon = {
                    if (!isLocked) ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
            )
            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
            ) {
                DropdownMenuItem(
                    text = { Text("— none —") },
                    onClick = {
                        onRegionChanged("")
                        expanded.value = false
                    },
                )
                TripData.regions.forEach { region ->
                    DropdownMenuItem(
                        text = { Text("${region.name} (${region.poiCount} POIs)") },
                        onClick = {
                            onRegionChanged(region.key)
                            expanded.value = false
                        },
                    )
                }
            }
        }
    }
}
