package com.rohitneel.todomaster.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.rohitneel.todomaster.data.model.CheckListModel
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel

@Composable
fun CheckListItem(
    item: CheckListModel,
    task: TaskModel?,
    taskViewModel: TaskViewModel,
    selectedFontFamily: FontFamily,
    selectedFontSize: TextUnit,
    selectedTextStyle: TextStyle,
    toggleUpperCase: Boolean,
    voiceInputText: String
) {
    var textFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(item.value))
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = taskViewModel.currentFocusRequestId.value) {
        if (item.uid == taskViewModel.currentFocusRequestId.value) {
            focusRequester.requestFocus()
        }
    }
    LaunchedEffect(voiceInputText, taskViewModel.currentFocusRequestId.value) {
        if (voiceInputText.isNotBlank() && item.uid == taskViewModel.currentFocusRequestId.value && textFieldValue.text != voiceInputText) {
            textFieldValue = TextFieldValue(voiceInputText)
            taskViewModel.onCheckListItemValueChange(item, voiceInputText)
        }
    }
    
    // Using a simplified version of formatText for now
    val formattedText = item.value // taskViewModel.fontStyleModel.isUpperCase etc logic should be applied
    
    if (textFieldValue.text != formattedText) {
        textFieldValue = textFieldValue.copy(text = formattedText)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Menu, // Placeholder for drag indicator
            contentDescription = ""
        )
        Checkbox(
            checked = item.checked || task?.isCompleted == true,
            onCheckedChange = {
                taskViewModel.onCheckListItemCheck(item, it)
            },
        )
        TextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                taskViewModel.onCheckListItemValueChange(item, it.text)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        taskViewModel.onFocusRequest(item)
                    }
                },
            textStyle = selectedTextStyle.copy(
                fontSize = selectedFontSize,
                fontFamily = selectedFontFamily,
                textDecoration = when {
                    item.checked && taskViewModel.fontStyleModel.isUnderlined ->
                        TextDecoration.combine(listOf(TextDecoration.LineThrough, TextDecoration.Underline))
                    item.checked -> TextDecoration.LineThrough
                    taskViewModel.fontStyleModel.isUnderlined -> TextDecoration.Underline
                    else -> TextDecoration.None
                }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrectEnabled = true,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = {
                taskViewModel.onAddCheckListItem(item.taskId, item)
            }),
            trailingIcon = {
                IconButton(
                    onClick = {
                        taskViewModel.onDeleteCheckListItem(item)
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Delete CheckListModel",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )
    }
}
