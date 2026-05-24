package com.rohitneel.todomaster.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rohitneel.todomaster.presentation.theme.AppColors
import com.rohitneel.todomaster.presentation.theme.TodoMasterTheme

@Composable
fun CustomCircularProgressBar(
	progress: Float = 10f,
	progressMax: Float = 100f,
	progressBarColor: Color = AppColors.Blue,
	progressBarWidth: Dp = 10.dp,
	backgroundProgressBarColor: Color = AppColors.LightGray,
	backgroundProgressBarWidth: Dp = 6.dp,
	roundBorder: Boolean = true,
	startAngle: Float = 0f
) {
	Canvas(modifier = Modifier.size(250.dp)) {

		val canvasSize = size.minDimension

		val radius = canvasSize / 2 - maxOf(
			backgroundProgressBarWidth,
			progressBarWidth
		).toPx() / 2

		drawCircle(
			color = backgroundProgressBarColor,
			radius = radius,
			center = size.center,
			style = Stroke(width = backgroundProgressBarWidth.toPx())
		)

		drawArc(
			color = progressBarColor,
			startAngle = 270f + startAngle,
			sweepAngle = (progress / progressMax) * 360f,
			useCenter = false,
			topLeft = size.center - Offset(
				radius,
				radius
			),
			size = Size(
				radius * 2,
				radius * 2
			),
			style = Stroke(
				width = progressBarWidth.toPx(),
				cap = if (roundBorder) StrokeCap.Round else StrokeCap.Butt
			)
		)
	}
}

@Preview
@Composable
fun CustomCircularProgressBarPreview() {
	TodoMasterTheme {
		CustomCircularProgressBar()
	}
}