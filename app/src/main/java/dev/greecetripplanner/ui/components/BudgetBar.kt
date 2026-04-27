package dev.greecetripplanner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.greecetripplanner.ui.screens.plan.DayBudget
import dev.greecetripplanner.util.formatHours

@Composable
fun BudgetBar(
    budget: DayBudget,
    modifier: Modifier = Modifier,
) {
    val statusColor = when (budget.status) {
        "ok" -> Color(0xFF00823B)
        "tight" -> Color(0xFFE89B00)
        else -> Color(0xFFC91432)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Breakdown row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                BudgetStat("Activities", formatHours(budget.activityH))
                BudgetStat("Transit", formatHours(budget.transitH))
                if (budget.driveH > 0) BudgetStat("Drive", formatHours(budget.driveH))
                BudgetStat("Available", formatHours(budget.availableH))
            }

            Spacer(Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { budget.percentage.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .semantics {
                        contentDescription = "Budget used ${(budget.percentage * 100).toInt()} percent"
                    },
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            )

            Spacer(Modifier.height(4.dp))

            // Status text
            val statusText = when {
                budget.freeH >= 0 -> "${formatHours(budget.freeH)} buffer"
                else -> "${formatHours(-budget.freeH)} over budget"
            }
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}

@Composable
private fun BudgetStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}
