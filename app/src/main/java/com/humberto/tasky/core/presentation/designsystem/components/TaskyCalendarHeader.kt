package com.humberto.tasky.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.humberto.tasky.core.presentation.designsystem.TaskyDarkGray
import com.humberto.tasky.core.presentation.designsystem.TaskyGray
import com.humberto.tasky.core.presentation.designsystem.TaskyLight3
import com.humberto.tasky.core.presentation.designsystem.TaskyOrange
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme
import java.time.LocalDate

@Composable
fun TaskyCalendarHeader(
    modifier: Modifier = Modifier,
    onSelectDate: (LocalDate) -> Unit,
    selectedDate: LocalDate,
) {
    val daysList = remember(selectedDate.month, selectedDate.year) {
        getDaysOfMonth(selectedDate)
    }
    val listState = rememberLazyListState()

    var isDateSelectedFromLazyRow by remember { mutableStateOf(false) }
    LaunchedEffect(selectedDate) {
        val selectedDateIndex = daysList.indexOfFirst { it == selectedDate }
        if (selectedDateIndex != -1 && !isDateSelectedFromLazyRow) {
            listState.scrollToItem(selectedDateIndex)
        }
        isDateSelectedFromLazyRow = false
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        LazyRow(
            state = listState,
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(daysList, key = { it }) { day ->
                DayItem(
                    day = day,
                    onSelectDate = {
                        isDateSelectedFromLazyRow = true
                        onSelectDate(day)
                    },
                    isSelected = day == selectedDate
                )
            }
        }
    }

}

@Composable
private fun DayItem(
    day: LocalDate,
    onSelectDate: (LocalDate) -> Unit,
    isSelected: Boolean
) {
    val backgroundColor = when {
        isSelected -> TaskyOrange
        day == LocalDate.now() -> TaskyLight3.copy(alpha = 0.2f)
        else -> Color.Transparent
    }
    val weekDayColor = if (isSelected) TaskyDarkGray else TaskyGray
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                onSelectDate(day)
            }
            .padding(vertical = 8.dp)
            .width(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.dayOfWeek.name.take(1),
            color = weekDayColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = day.dayOfMonth.toString(),
            color = TaskyDarkGray,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun getDaysOfMonth(date: LocalDate): List<LocalDate> {
    val firstDayOfMonth = date.withDayOfMonth(1)
    val daysInMonth = date.lengthOfMonth()
    return (0 until daysInMonth).map { firstDayOfMonth.plusDays(it.toLong()) }
}

@Preview
@Composable
private fun TaskyCalendarHeaderPreview() {
    TaskyTheme {
        TaskyCalendarHeader(
            onSelectDate = { },
            selectedDate = LocalDate.now().withDayOfMonth(2)
        )
    }
}