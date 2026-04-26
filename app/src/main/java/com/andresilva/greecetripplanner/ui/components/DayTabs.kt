package com.andresilva.greecetripplanner.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.andresilva.greecetripplanner.data.TripData
import com.andresilva.greecetripplanner.data.model.TripDay

@Composable
fun DayTabs(
    days: List<TripDay>,
    activeDay: Int,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    ScrollableTabRow(
        selectedTabIndex = activeDay,
        modifier = modifier.fillMaxWidth(),
        edgePadding = 8.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        days.forEachIndexed { index, day ->
            val dayColor = TripData.dayColors.getOrNull(index)?.let {
                Color(it)
            } ?: MaterialTheme.colorScheme.primary

            Tab(
                selected = index == activeDay,
                onClick = { onDaySelected(index) },
                text = {
                    Text(
                        text = "D${index}",
                        fontWeight = if (index == activeDay) FontWeight.Bold else FontWeight.Normal,
                    )
                },
                selectedContentColor = dayColor,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
