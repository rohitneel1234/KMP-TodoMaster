package com.rohitneel.todomaster.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohitneel.todomaster.data.model.CheckListModel
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.theme.Clarendon
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.border

@Composable
fun DisplayCheckListItem(checkList: CheckListModel, task: TaskModel) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (checkList.checked || task.isCompleted) {
             Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface
            )
        } else {
             // Placeholder for blank checkbox
             Box(modifier = Modifier.size(16.dp).border(1.dp, MaterialTheme.colorScheme.onSurface))
        }
        
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = checkList.value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontFamily = Clarendon,
            fontWeight = FontWeight.Medium,
            textDecoration = if (checkList.checked || task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
        )
    }
}
