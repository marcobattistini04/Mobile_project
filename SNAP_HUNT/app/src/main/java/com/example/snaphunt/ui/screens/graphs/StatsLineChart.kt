package com.example.snaphunt.ui.screens.graphs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatsLineChart(
    points: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val maxPoints = points.maxOf { it.second }.takeIf { it > 0f } ?: 1f

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(color = Color.Gray, fontSize = 10.sp)

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val width = size.width
            val height = size.height

            val paddingLeft = 70f
            val paddingBottom = 50f
            val chartWidth = width - paddingLeft
            val chartHeight = height - paddingBottom

            val axisColor = Color.Gray.copy(alpha = 0.3f)

            val gridLevels = 3
            for (i in 0..gridLevels) {
                val fraction = i.toFloat() / gridLevels
                val yCoord = chartHeight - (fraction * (chartHeight - 20f))
                val valueLabel = (fraction * maxPoints).toInt().toString()

                drawText(
                    textMeasurer = textMeasurer,
                    text = valueLabel,
                    style = labelStyle,
                    topLeft = Offset(10f, yCoord - 15f)
                )

                drawLine(
                    color = axisColor,
                    start = Offset(paddingLeft, yCoord),
                    end = Offset(width, yCoord),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }


            drawLine(
                color = Color.Gray,
                start = Offset(paddingLeft, 0f),
                end = Offset(paddingLeft, chartHeight),
                strokeWidth = 3f
            )

            drawLine(
                color = Color.Gray,
                start = Offset(paddingLeft, chartHeight),
                end = Offset(width, chartHeight),
                strokeWidth = 3f
            )

            val spaceX = if (points.size > 1) chartWidth / (points.size - 1) else chartWidth
            val coordinates = points.mapIndexed { index, (_, value) ->
                val x = paddingLeft + (index * spaceX)
                val ratioY = value / maxPoints
                val y = chartHeight - (ratioY * (chartHeight - 20f))
                Offset(x, y)
            }

            val linePath = Path().apply {
                if (coordinates.isNotEmpty()) {
                    moveTo(coordinates.first().x, coordinates.first().y)
                    for (i in 1 until coordinates.size) {
                        lineTo(coordinates[i].x, coordinates[i].y)
                    }
                }
            }

            drawPath(
                path = linePath,
                color = Color(0xFFF2531B),
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )

            coordinates.forEach { centerOffset ->
                drawCircle(
                    color = Color.White,
                    radius = 10f,
                    center = centerOffset
                )
                drawCircle(
                    color = Color(0xFF444141),
                    radius = 6f,
                    center = centerOffset
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEach { (label, _) ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}