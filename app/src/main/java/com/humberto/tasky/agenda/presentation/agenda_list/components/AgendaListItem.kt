package com.humberto.tasky.agenda.presentation.agenda_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.humberto.tasky.R
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.presentation.agenda_details.ReminderType
import com.humberto.tasky.core.presentation.designsystem.TaskyBlack
import com.humberto.tasky.core.presentation.designsystem.TaskyBrown
import com.humberto.tasky.core.presentation.designsystem.TaskyDarkGray
import com.humberto.tasky.core.presentation.designsystem.TaskyGreen
import com.humberto.tasky.core.presentation.designsystem.TaskyLight2
import com.humberto.tasky.core.presentation.designsystem.TaskyLightGreen
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme
import com.humberto.tasky.core.presentation.designsystem.TaskyWhite
import com.humberto.tasky.core.presentation.designsystem.components.MoreButtonWithDropDownMenu
import com.humberto.tasky.core.presentation.designsystem.components.TaskyRadioButton
import com.humberto.tasky.core.presentation.designsystem.components.util.DropDownItem
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AgendaListItem(
    onCheckItem: () -> Unit = { },
    onOpenItem: () -> Unit,
    onEditItem: () -> Unit,
    onDeleteItem: () -> Unit,
    agendaItem: AgendaItem,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (agendaItem) {
        is AgendaItem.Event -> TaskyLightGreen
        is AgendaItem.Reminder -> TaskyLight2
        is AgendaItem.Task -> TaskyGreen
    }

    val foregroundColor = when (agendaItem) {
        is AgendaItem.Task -> TaskyWhite
        else -> TaskyBlack
    }

    val textColor = when (agendaItem) {
        is AgendaItem.Task -> TaskyWhite
        else -> TaskyDarkGray
    }

    val moreButtonColor = when (agendaItem) {
        is AgendaItem.Task -> TaskyWhite
        else -> TaskyBrown
    }

    val isSelected = when (agendaItem) {
        is AgendaItem.Task -> agendaItem.isDone
        else -> false
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onOpenItem() }
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                TaskyRadioButton(
                    selected = isSelected,
                    enabled = agendaItem is AgendaItem.Task,
                    onClick = { onCheckItem() },
                    radioButtonColor = foregroundColor,
                    modifier = Modifier.padding(2.dp)
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = agendaItem.title.ifEmpty { stringResource(id = R.string.no_title) },
                        modifier = Modifier,
                        color = foregroundColor,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.W600,
                            textDecoration =
                                if (isSelected) TextDecoration.LineThrough
                                else TextDecoration.None
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = agendaItem.description ?: "",
                        color = textColor,
                        style = MaterialTheme.typography.bodyLarge,
                        minLines = 2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            MoreButtonWithDropDownMenu(
                menuItems = listOf(
                    DropDownItem(
                        title = stringResource(id = R.string.open),
                        onClick = onOpenItem,
                    ),
                    DropDownItem(
                        title = stringResource(id = R.string.edit),
                        onClick = onEditItem,
                    ),
                    DropDownItem(
                        title = stringResource(id = R.string.delete),
                        onClick = onDeleteItem,
                    ),
                ),
                iconColor = moreButtonColor,
            )
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            color = textColor,
            textAlign = TextAlign.End,
            text = agendaItem.from.toFormattedDateTime(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun ZonedDateTime.toFormattedDateTime(): String {
    val timeInLocalTime = this
        .withZoneSameInstant(ZoneId.systemDefault())
    return DateTimeFormatter
        .ofPattern("MMM dd, HH:mm")
        .format(timeInLocalTime)
}

@Preview
@Composable
private fun AgendaListItemPreview() {
    TaskyTheme {
        AgendaListItem(
            agendaItem = AgendaItem.Task(
                id = "1",
                title = "Meeting",
                description = "Description",
                from = ZonedDateTime.now(),
                reminderType = ReminderType.ThirtyMinutes,
                isDone = true
            ),
            onOpenItem = { },
            onEditItem = { },
            onDeleteItem = { }
        )
    }
}