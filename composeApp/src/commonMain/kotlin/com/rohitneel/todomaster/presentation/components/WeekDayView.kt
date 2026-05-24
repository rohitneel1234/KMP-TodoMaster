package com.rohitneel.todomaster.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.core.WeekDay
import com.rohitneel.todomaster.presentation.theme.AppColors.Blue
import com.rohitneel.todomaster.presentation.theme.Clarendon
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun WeekDayView(
	day: WeekDay,
	selected: Boolean = false,
	indicator: Boolean = true,
	themeColor: Color = Blue,
	onClick: (LocalDate) -> Unit = {},
) {
	val textColor = if (selected) {
		Color.Black // Simplified
	} else if (indicator) {
		themeColor
	} else {
		MaterialTheme.colorScheme.onPrimary
	}
    
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

	Box(
		modifier = Modifier
			.width(50.dp) // Simplified from LocalConfiguration
			.padding(4.dp)
			.clip(RoundedCornerShape(12.dp))
			.background(if (selected) themeColor else MaterialTheme.colorScheme.secondary)
			.border(
				width = if (day.date == today) 2.dp else (-1).dp,
				color = themeColor,
				shape = RoundedCornerShape(12.dp)
			)
			.clickable { onClick(day.date) },
		contentAlignment = Alignment.Center,
	) {
		Column(
			modifier = Modifier.padding(bottom = 10.dp, top = 6.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Text(
				text = day.date.dayOfMonth.toString(),
				fontSize = 18.sp,
				fontFamily = Clarendon,
				fontWeight = FontWeight.SemiBold,
				color = textColor
			)
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				text = day.date.dayOfWeek.name.take(3),
				fontSize = 12.sp,
				fontFamily = Clarendon,
				color = textColor,
			)
		}
	}
}
