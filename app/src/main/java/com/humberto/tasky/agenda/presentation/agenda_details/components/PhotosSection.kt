package com.humberto.tasky.agenda.presentation.agenda_details.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.humberto.tasky.R
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.event.EventPhoto
import com.humberto.tasky.core.presentation.designsystem.PlusIcon
import com.humberto.tasky.core.presentation.designsystem.TaskyGray
import com.humberto.tasky.core.presentation.designsystem.TaskyLight
import com.humberto.tasky.core.presentation.designsystem.TaskyLight2
import com.humberto.tasky.core.presentation.designsystem.TaskyLightBlue
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme

@Composable
fun PhotosSection(
    modifier: Modifier = Modifier,
    photos: List<EventPhoto>,
    onAddNewPhotoClick: () -> Unit,
    onPhotoClick: (EventPhoto) -> Unit,
    canEditPhotos: Boolean,
    maxPhotoAmount: Int = AgendaItem.Event.MAX_PHOTO_AMOUNT
) {
    if (photos.isEmpty() && canEditPhotos) {
        Box(
            modifier = modifier
                .clickable { onAddNewPhotoClick() }
                .background(TaskyLight),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = PlusIcon,
                    contentDescription = null,
                    tint = TaskyGray
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(id = R.string.add_photos),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TaskyGray
                )
            }
        }
    } else {
        Column(
            modifier = modifier
                .background(TaskyLight)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.photos),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            PhotoGrid(
                photos = photos,
                modifier = Modifier.fillMaxWidth(),
                onAddNewPhotoClick = onAddNewPhotoClick,
                onPhotoClick = onPhotoClick,
                maxPhotoAmount = maxPhotoAmount,
                canEditPhotos = canEditPhotos
            )
        }
    }
}


@Composable
private fun PhotoGrid(
    photos: List<EventPhoto>,
    onPhotoClick: (EventPhoto) -> Unit,
    onAddNewPhotoClick: () -> Unit,
    canEditPhotos: Boolean,
    modifier: Modifier = Modifier,
    columnCount: Int = 5,
    maxPhotoAmount: Int = AgendaItem.Event.MAX_PHOTO_AMOUNT
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(photos.size / columnCount + 1) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (col in 0 until columnCount) {
                    val photo = photos.getOrNull(columnCount * row + col)
                    if (photo != null && columnCount * row + col < maxPhotoAmount) {
                        EventPhoto(
                            photo = photo,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onPhotoClick(photo) }
                        )
                    } else {
                        if (photos.size >= maxPhotoAmount) {
                            repeat(columnCount - col) {
                                // Occupy remaining space to not mess up the widths of the last row
                                Spacer(modifier = Modifier.weight(1f))
                            }
                            break
                        }
                        if(canEditPhotos) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        onAddNewPhotoClick()
                                    }
                                    .background(TaskyLightBlue)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TaskyLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(id = R.string.add_photos),
                                    tint = TaskyLightBlue
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        repeat(columnCount - col - 1) {
                            // Occupy remaining space to not mess up the widths of the last row
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        break
                    }
                }
            }
        }
    }
}

@Composable
fun EventPhoto(
    photo: EventPhoto,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = when (photo) {
            is EventPhoto.Remote -> photo.photoUrl
            is EventPhoto.Local -> Uri.parse(photo.uriString)
        },
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(TaskyLightBlue)
            .padding(2.dp)
            .clip(RoundedCornerShape(6.dp))
    )
}

@Preview
@Composable
private fun PhotosSectionPreview() {
    TaskyTheme {
        PhotosSection(
            photos = listOf(),
            onPhotoClick = {},
            onAddNewPhotoClick = { },
            canEditPhotos = true
        )
    }
}