package com.example.snaphunt.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snaphunt.R
import java.time.DayOfWeek

@Composable
fun MultiplierStreakCard(
    modifier: Modifier = Modifier,
    multiplier: Int,
    activeDays: Set<DayOfWeek>
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Riga con Fulmine e Titolo
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_bolt), // Assicurati che il nome sia corretto
                            contentDescription = null,
                            modifier = Modifier.size(25.dp).padding(end = 6.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Weekly Multiplier",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Complete challenges every day consecutively to improve it!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Cerchio del moltiplicatore
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.inverseSurface)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "x$multiplier",
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Riga dei giorni
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val daysOfWeekMap = listOf(
                    DayOfWeek.MONDAY to "Mon", DayOfWeek.TUESDAY to "Tue",
                    DayOfWeek.WEDNESDAY to "Wed", DayOfWeek.THURSDAY to "Thu",
                    DayOfWeek.FRIDAY to "Fri", DayOfWeek.SATURDAY to "Sat", DayOfWeek.SUNDAY to "Sun"
                )

                daysOfWeekMap.forEach { (dayOfWeek, dayLabel) ->
                    val isActived = activeDays.contains(dayOfWeek)

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isActived) MaterialTheme.colorScheme.onSurface else Color.Transparent)
                            .then(
                                if (!isActived) Modifier.border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayLabel,
                            color = if (isActived) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}