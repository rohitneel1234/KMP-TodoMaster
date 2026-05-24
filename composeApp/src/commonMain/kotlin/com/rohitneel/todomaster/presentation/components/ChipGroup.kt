package com.rohitneel.todomaster.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel

@Composable
fun ChipGroup(
    items: List<String>,
    selectedIndex: Int,
    taskViewModel: TaskViewModel,
    selectedContainerColor: Color = Color(taskViewModel.themeColor.value.toArgb()),
    onSelectedChanged: (Int) -> Unit = {},
) {
    Column {
        LazyRow(
            horizontalArrangement = Arrangement.End
        ) {
            itemsIndexed(items) { index, item ->
                FilterChip(
                    label = {
                        Text(item)
                    },
                    colors = SelectableChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        selectedContainerColor = selectedContainerColor,
                        selectedLabelColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                        disabledSelectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                        selectedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        leadingIconColor = MaterialTheme.colorScheme.onSurface,
                        trailingIconColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(horizontal = 4.dp),
                    selected = if (selectedIndex in items.indices) items[selectedIndex] == item else false,
                    onClick = {
                        onSelectedChanged(index)
                    }
                )
            }
        }
    }
}