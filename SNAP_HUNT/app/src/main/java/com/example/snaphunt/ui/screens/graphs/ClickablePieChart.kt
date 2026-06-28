package com.example.snaphunt.ui.screens.graphs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import androidx.compose.ui.geometry.Size

@Composable
fun ClickablePieChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    onSliceClick: (String) -> Unit
) {
    val total = data.sumOf { it.second.toDouble() }.toFloat().takeIf { it > 0f } ?: 1f

    val colors = listOf(
        Color(0xFF444141),
        Color(0xFFF2531B),
        Color(0xFF3A629F)
    )

    var selected by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(data) {
                        detectTapGestures { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val angle = Math.toDegrees(
                                atan2((offset.y - center.y).toDouble(), (offset.x - center.x).toDouble())
                            ).toFloat()

                            val touchAngle = (angle + 360f) % 360f
                            var startAngle = 0f

                            val clicked = data.firstNotNullOfOrNull { (label, value) ->
                                val sweep = (value / total) * 360f
                                val end = startAngle + sweep
                                val result = if (touchAngle in startAngle..end) label else null
                                startAngle += sweep
                                result
                            }

                            clicked?.let {
                                selected = it
                                onSliceClick(it)
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    var startAngle = 0f
                    val strokeWidth = 32f

                    val minDimension = size.minDimension - strokeWidth
                    val arcSize = Size(minDimension, minDimension)

                    val topLeftOffset = Offset(
                        x = (size.width - minDimension) / 2f,
                        y = (size.height - minDimension) / 2f
                    )

                    data.forEachIndexed { index, _ ->
                        val value = data[index].second
                        val sweep = (value / total) * 360f
                        if (sweep == 0f) return@forEachIndexed

                        val sliceColor = colors[index % colors.size]

                        drawArc(
                            color = sliceColor,
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeftOffset,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )

                        startAngle += sweep
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${total.toInt()}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total challenges",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEachIndexed { index, (label, value) ->
                val percentage = ((value / total) * 100).toInt()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "${value.toInt()} ($percentage%)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors[index % colors.size]
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}