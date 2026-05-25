package com.rohitneel.todomaster.presentation.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.theme.Clarendon
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
actual fun LinkableText(
    task: TaskModel,
    description: String,
    color: Color,
    modifier: Modifier,
) {
    val urlRegex = "(http|https|www|ftp)\\S+".toRegex(RegexOption.IGNORE_CASE)
    val matches = urlRegex.findAll(description)
    
    if (matches.any()) {
        val annotatedString = buildAnnotatedString {
            var lastIndex = 0
            for (match in matches) {
                append(description.substring(lastIndex, match.range.first))
                val url = match.value
                pushStringAnnotation(tag = "URL", annotation = url)
                withStyle(
                    style = SpanStyle(
                        color = Color.Blue,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.Underline
                    )
                ) {
                    append(url)
                }
                pop()
                lastIndex = match.range.last + 1
            }
            append(description.substring(lastIndex))
        }
        
        ClickableText(
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        val url = if (annotation.item.startsWith("www")) "http://${annotation.item}" else annotation.item
                        val nsUrl = NSURL.URLWithString(url)
                        if (nsUrl != null) {
                            UIApplication.sharedApplication.openURL(nsUrl)
                        }
                    }
            },
            modifier = modifier,
            maxLines = 5,
            style = TextStyle(
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Clarendon,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            )
        )
    } else {
        Text(
            text = description,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Clarendon,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            modifier = modifier
        )
    }
}
