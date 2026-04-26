package com.andresilva.greecetripplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andresilva.greecetripplanner.data.TripData
import com.andresilva.greecetripplanner.data.model.Poi
import com.andresilva.greecetripplanner.util.formatHours

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PoiCard(
    poi: Poi,
    isAdded: Boolean,
    addedOnDay: Int?,
    isOverBudget: Boolean,
    onAdd: () -> Unit,
    onMove: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val alpha = if (isAdded) 0.5f else 1f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isAdded) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.outline
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top: name + hours
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = poi.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                ) {
                    Text(
                        text = formatHours(poi.hours),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // Description
            Text(
                text = poi.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            // Tip
            if (poi.tip != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = poi.tip,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE89B00),
                    ),
                )
            }

            Spacer(Modifier.height(6.dp))

            // Links row
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                LinkChip(icon = Icons.Default.Map, label = "Map") {
                    uriHandler.openUri(poi.mapsUrl)
                }
                LinkChip(icon = Icons.Default.Search, label = "Info") {
                    uriHandler.openUri(poi.searchUrl)
                }
                LinkChip(icon = Icons.Default.Image, label = "Photos") {
                    uriHandler.openUri(poi.imagesUrl)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Bottom: tags + add/move button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    poi.categories.forEach { cat ->
                        CategoryTag(cat)
                    }
                }

                if (isAdded && onMove != null && addedOnDay != null) {
                    TextButton(onClick = onMove) {
                        Text(
                            "↩ D$addedOnDay",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        )
                    }
                } else if (!isAdded) {
                    TextButton(onClick = onAdd) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = if (isOverBudget) "Add ⚠" else "Add",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isOverBudget) Color(0xFFE89B00) else MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            if (isOverBudget && !isAdded) {
                Text(
                    "⚠ Would exceed time budget",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFE89B00),
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }
    }
}

@Composable
fun LinkChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary))
        }
    }
}

@Composable
fun CategoryTag(categoryKey: String) {
    val cat = TripData.categoryMap[categoryKey] ?: return
    val color = try { Color(android.graphics.Color.parseColor(cat.colorHex)) } catch (_: Exception) { MaterialTheme.colorScheme.primary }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
    ) {
        Text(
            text = "${cat.icon} ${cat.label}",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                color = color,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}
