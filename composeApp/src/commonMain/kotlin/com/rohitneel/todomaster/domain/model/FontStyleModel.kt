package com.rohitneel.todomaster.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FontStyleModel(
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderlined: Boolean = false,
    val alignmentSelected: Boolean = false,
    val textColorSelected: Boolean = false,
    val isUpperCase: Boolean = false
)
