package dev.greecetripplanner.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.greecetripplanner.data.TripData
import dev.greecetripplanner.data.model.TripTemplate

@Composable
fun TemplateBar(
    onTemplateSelected: (TripTemplate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        TripData.templates.forEach { template ->
            FilterChip(
                selected = false,
                onClick = { onTemplateSelected(template) },
                label = {
                    Text(
                        text = "${template.icon} ${template.name}",
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
            )
        }
    }
}
