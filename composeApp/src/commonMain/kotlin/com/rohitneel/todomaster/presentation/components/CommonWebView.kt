package com.rohitneel.todomaster.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CommonWebView(
    modifier: Modifier = Modifier,
    url: String,
    onPageFinished: () -> Unit = {}
)
