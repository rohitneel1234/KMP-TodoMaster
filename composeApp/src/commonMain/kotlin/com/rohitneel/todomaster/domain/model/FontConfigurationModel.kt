package com.rohitneel.todomaster.domain.model

import org.jetbrains.compose.resources.FontResource

data class FontProperties(
    val isBold: Boolean,
    val isItalic: Boolean,
    val isUnderlined: Boolean,
    val alignmentSelected: Boolean,
    val textColorSelected: Boolean,
    val isUpperCase: Boolean,
    val onBoldClick: () -> Unit,
    val onItalicClick: () -> Unit,
    val onUnderlineClick: () -> Unit,
    val onTextColorClick: () -> Unit,
    val onCenterAlignClick: () -> Unit,
    val onUpperCaseClick: () -> Unit
)

data class FontFamilyOption(
    val name: String,
    val fontFamily: FontResource
)

enum class BottomSheetType {
    COLOR,
    FONT
}
