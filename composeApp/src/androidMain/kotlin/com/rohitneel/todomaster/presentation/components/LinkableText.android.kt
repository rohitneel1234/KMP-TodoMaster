package com.rohitneel.todomaster.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.theme.Clarendon
import java.util.Locale
import java.util.regex.Pattern

@Composable
actual fun LinkableText(
    task: TaskModel,
    description: String,
    color: Color,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val urlPattern = remember { Pattern.compile("(http|https|www|ftp)\\S+", Pattern.CASE_INSENSITIVE) }
    val matcher = urlPattern.matcher(description)
    val containsUrl = matcher.find()
    
    if (containsUrl) {
        val annotatedString = buildAnnotatedString {
            matcher.reset()
            var start = 0
            while (matcher.find()) {
                append(description.substring(start, matcher.start()))
                val url = matcher.group()
                withStyle(
                    style = SpanStyle(
                        color = Color.Blue,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.Underline
                    )
                ) {
                    append(url)
                }
                addStringAnnotation(
                    tag = "URL",
                    annotation = url,
                    start = length - url.length,
                    end = length
                )
                start = matcher.end()
            }
            append(description.substring(start))
        }
        ClickableText(
            text = annotatedString,
            onClick = { offset ->
                val urlAnnotation = annotatedString.getStringAnnotations(
                    tag = "URL",
                    start = offset,
                    end = offset
                ).firstOrNull()
                if (urlAnnotation != null) {
                    val url = urlAnnotation.item.lowercase(Locale.ROOT)
                    val uriIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    ContextCompat.startActivity(context, uriIntent, null)
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
