package com.example.snaphunt.ui.components

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.snaphunt.photos.ChallengeFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBar(currentFilter: ChallengeFilter, onFilterSelected: (ChallengeFilter) -> Unit) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .height(IntrinsicSize.Min)

    ) {
        ChallengeFilter.entries.forEachIndexed { index, filter ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = ChallengeFilter.entries.size),
                onClick = { onFilterSelected(filter) },
                selected = currentFilter == filter,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.onSurface,
                    activeContentColor = MaterialTheme.colorScheme.surface,
                    inactiveContainerColor = MaterialTheme.colorScheme.surface,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurface,
                    activeBorderColor = MaterialTheme.colorScheme.onSurface,
                    inactiveBorderColor = MaterialTheme.colorScheme.onSurface
                ),
                label = {
                    Text(
                        text = when(filter) {
                            ChallengeFilter.ALL -> "ALL"
                            ChallengeFilter.COMPLETED -> "Completed"
                            ChallengeFilter.SKIPPED -> "Skipped"
                        },
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )
        }
    }
}