package com.example.snaphunt.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors()

) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = colors
    ) {
        Text(
            text = text,
            maxLines = 1,
            fontSize = 13.sp,
            overflow = TextOverflow.Ellipsis
        )
    }
}