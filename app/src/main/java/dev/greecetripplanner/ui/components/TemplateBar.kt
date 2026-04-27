package dev.greecetripplanner.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.greecetripplanner.data.TripData
import dev.greecetripplanner.data.model.TripTemplate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TemplateBar(
    activeTemplate: String?,
    customTemplates: List<TripTemplate>,
    onTemplateSelected: (TripTemplate) -> Unit,
    onCreateCustom: () -> Unit,
    onDeleteCustom: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val deleteTarget = remember { mutableStateOf<TripTemplate?>(null) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // Built-in templates
        TripData.templates.forEach { template ->
            FilterChip(
                selected = activeTemplate == template.key,
                onClick = { onTemplateSelected(template) },
                label = {
                    Text(
                        text = "${template.icon} ${template.name}",
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
            )
        }

        // Custom templates
        customTemplates.forEach { template ->
            FilterChip(
                selected = activeTemplate == template.key,
                onClick = { onTemplateSelected(template) },
                label = {
                    Text(
                        text = "${template.icon} ${template.name}",
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.combinedClickable(
                    onClick = { onTemplateSelected(template) },
                    onLongClick = { deleteTarget.value = template },
                ),
            )
        }

        // "+" create button
        AssistChip(
            onClick = onCreateCustom,
            label = { Text("＋ New", style = MaterialTheme.typography.labelMedium) },
        )
    }

    // Delete confirmation dialog
    deleteTarget.value?.let { template ->
        AlertDialog(
            onDismissRequest = { deleteTarget.value = null },
            title = { Text("Delete route?") },
            text = { Text("Remove \"${template.name}\" custom route?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteCustom(template.key)
                    deleteTarget.value = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget.value = null }) { Text("Cancel") }
            },
        )
    }
}
