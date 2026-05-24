package com.rohitneel.todomaster.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.rohitneel.todomaster.presentation.theme.AppColors.Blue
import com.rohitneel.todomaster.presentation.theme.Clarendon
import com.rohitneel.todomaster.presentation.theme.DarkThemeColors.DarkGray
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun MonthDayView(
	day: CalendarDay,
	selected: Boolean,
	indicator: Boolean = true,
	isDatePicker: Boolean = false,
	themeColor: Color = Blue,
	onClick: (LocalDate) -> Unit = {}
) {
	val textColor = if (selected) {
		Color.Black // Simplified
	} else {
		if (day.position == DayPosition.MonthDate) {
			MaterialTheme.colorScheme.onSurface
		} else {
			DarkGray
		}
	}
	val dayTextStyle = if (isDatePicker) {
		androidx.compose.ui.text.TextStyle(
			fontSize = 14.sp,
			fontFamily = Clarendon
		)
	} else {
		androidx.compose.ui.text.TextStyle(
			fontSize = 16.sp,
			fontFamily = Clarendon,
		)
	}
    
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

	Box(
		modifier = Modifier
			.aspectRatio(1f)
			.padding(6.dp)
			.clip(RoundedCornerShape(8.dp))
			.background(if (selected) themeColor else Color.Transparent)
			.border(
				width = if (day.date == today) 2.dp else (-1).dp,
				color = themeColor,
				shape = RoundedCornerShape(8.dp)
			)
			.clickable { onClick(day.date) },
		contentAlignment = Alignment.Center
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = day.date.dayOfMonth.toString(),
				style = dayTextStyle,
				color = textColor
			)
			Box(
				modifier = Modifier
					.size(4.dp)
					.clip(CircleShape)
					.background(if (indicator && day.position == DayPosition.MonthDate) themeColor else Color.Transparent)
			)
		}
	}
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
	Row(modifier = Modifier.fillMaxWidth()) {
		for (dayOfWeek in daysOfWeek) {
			Text(
				modifier = Modifier.weight(1f),
				textAlign = TextAlign.Center,
				fontSize = 14.sp,
				fontFamily = Clarendon,
				color = MaterialTheme.colorScheme.onSurface,
				text = dayOfWeek.name.take(3), // Simplified
			)
		}
	}
}
