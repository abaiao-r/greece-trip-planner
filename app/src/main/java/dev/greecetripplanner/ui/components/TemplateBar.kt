package dev.greecetripplanner.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.greecetripplanner.data.TripData
import dev.greecetripplanner.data.model.TripTemplate

private data class RouteGroup(
    val label: String,
    val color: Color,
    val templateKeys: List<String>,
)

private val routeGroups = listOf(
    RouteGroup("A Monument", Color(0xFF533483), listOf("northern-cultural", "history-deep")),
    RouteGroup("B Coast", Color(0xFF009A97), listOf("coast-beach", "food-and-beach")),
    RouteGroup("C Nature", Color(0xFF00823B), listOf("zagori-adventure", "olympus-pelion", "epirus-explorer")),
    RouteGroup("D Pelop.", Color(0xFFB45309), listOf("peloponnese")),
    RouteGroup("Mix", Color(0xFF1C69D4), listOf("grand-tour")),
)

@Composable
fun TemplateBar(
    activeTemplateKey: String?,
    onTemplateSelected: (TripTemplate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Route group labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            routeGroups.forEach { group ->
                Text(
                    text = group.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = group.color,
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }

        // Template chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            TripData.templates.forEach { template ->
                val group = routeGroups.find { template.key in it.templateKeys }
                val isActive = template.key == activeTemplateKey
                FilterChip(
                    selected = isActive,
                    onClick = { onTemplateSelected(template) },
                    label = {
                        Text(
                            text = "${template.icon} ${template.name}",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    colors = if (isActive && group != null) {
                        FilterChipDefaults.filterChipColors(
                            selectedContainerColor = group.color.copy(alpha = 0.15f),
                            selectedLabelColor = group.color,
                        )
                    } else {
                        FilterChipDefaults.filterChipColors()
                    },
                )
            }
        }
    }
}
