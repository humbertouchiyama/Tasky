package com.humberto.tasky.agenda.presentation.components

import androidx.compose.foundation.background
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
import com.humberto.tasky.agenda.presentation.CardStyle
import com.humberto.tasky.agenda.presentation.EventType
import com.humberto.tasky.agenda.presentation.model.AgendaItemUi
import com.humberto.tasky.core.presentation.designsystem.TaskyBrown
import com.humberto.tasky.core.presentation.designsystem.TaskyLightGreen
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme
import com.humberto.tasky.core.presentation.designsystem.components.MoreButtonWithDropDownMenu
import com.humberto.tasky.core.presentation.designsystem.components.TaskyRadioButton
import com.humberto.tasky.core.presentation.designsystem.components.util.DropDownItem

@Composable
fun AgendaListItem(
    modifier: Modifier = Modifier,
    onCheckItem: () -> Unit = { },
    onOpenItem: () -> Unit,
    onEditItem: () -> Unit,
    onDeleteItem: () -> Unit,
    agendaItemUi: AgendaItemUi
) {
    val cardStyle = getCardStyle(agendaItemUi.eventType)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardStyle.backgroundColor)
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
                    selected = agendaItemUi.isItemChecked == true,
                    enabled = agendaItemUi.isItemCheckable,
                    onClick = { onCheckItem() },
                    radioButtonColor = cardStyle.titleColor,
                    modifier = Modifier.padding(2.dp)
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = agendaItemUi.title,
                        modifier = Modifier,
                        color = cardStyle.titleColor,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.W600,
                            textDecoration =
                                if (agendaItemUi.isItemChecked == true) TextDecoration.LineThrough
                                else TextDecoration.None
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = agendaItemUi.description,
                        color = cardStyle.descriptionColor,
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
                iconColor = cardStyle.moreButtonColor,
            )
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            color = cardStyle.descriptionColor,
            textAlign = TextAlign.End,
            text = agendaItemUi.dateTime,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun getCardStyle(eventType: EventType): CardStyle {
    return when (eventType) {
        is EventType.Task -> CardStyle(
            backgroundColor = MaterialTheme.colorScheme.secondary,
            titleColor = MaterialTheme.colorScheme.onPrimary,
            descriptionColor = MaterialTheme.colorScheme.onSecondary,
            moreButtonColor = MaterialTheme.colorScheme.onSecondary
        )
        is EventType.Event -> CardStyle(
            backgroundColor = TaskyLightGreen,
            titleColor = MaterialTheme.colorScheme.onSurface,
            descriptionColor = MaterialTheme.colorScheme.onSurfaceVariant,
            moreButtonColor = TaskyBrown
        )
        is EventType.Reminder -> CardStyle(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            titleColor = MaterialTheme.colorScheme.onSurface,
            descriptionColor = MaterialTheme.colorScheme.onSurfaceVariant,
            moreButtonColor = TaskyBrown
        )
    }
}

@Preview
@Composable
private fun AgendaListItemPreview() {
    TaskyTheme {
        AgendaListItem(
            agendaItemUi = AgendaItemUi(
                title = "Meeting",
                description = "Description",
                dateTime = "Mar 5, 10:30",
                isItemChecked = true,
                eventType = EventType.Task
            ),
            onOpenItem = { },
            onEditItem = { },
            onDeleteItem = { }
        )
    }
}