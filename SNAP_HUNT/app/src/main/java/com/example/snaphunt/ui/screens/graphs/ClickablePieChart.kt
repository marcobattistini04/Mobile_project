package com.example.snaphunt.ui.screens.graphs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.selects.select
import kotlin.math.atan2


@Composable
fun ClickablePieChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    onSliceClick: (String) -> Unit
) {
    val total = data.sumOf { it.second.toDouble() }.toFloat()

    val colors = listOf(
        Color(0xFFEF5350),
        Color(0xFF42A5F5),
        Color(0xFF66BB6A),
        Color(0xFFFFCA28),
        Color(0xFFAB47BC)
    )

    val slices = remember(data) {
        var start = 0f

        data.map { (label, value) ->
            val sweep = (value / total) * 360f
            val slice = Triple(label, start, sweep)
            start += sweep
            slice
        }
    }

    var selected by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier.pointerInput(slices) {
            detectTapGestures { offset ->

                val center = Offset(size.width / 2f, size.height / 2f)

                val angle = Math.toDegrees(
                    atan2(
                        (offset.y - center.y).toDouble(),
                        (offset.x - center.x).toDouble()
                    )
                ).toFloat()

                val touchAngle = (angle + 360f) % 360f

                var startAngle = 0f

                val clicked = data.firstNotNullOfOrNull { (label, value) ->
                    val sweep = (value / total) * 360f
                    val end = startAngle + sweep
                    val result =
                        if (touchAngle in startAngle..end) label else null
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
            val shadowOffset = 10f

            data.forEachIndexed { index, (_, value) ->
                val sweep = (value / total) * 360f

                val scale = if (selected == data[index].first) 1.05f else 1f

                //shadow
                drawArc(
                    color = Color.Black.copy(alpha = 0.15f),
                    startAngle = startAngle + 2,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = Offset(0f, shadowOffset)
                )

                // slice
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = Offset(0f, 6f)
                )

                drawArc(
                    color =  colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true
                )

                startAngle += sweep
            }

            //donut
            drawCircle(
                color = Color.White,
                radius = size.minDimension / 8f
            )
        }
    }
}