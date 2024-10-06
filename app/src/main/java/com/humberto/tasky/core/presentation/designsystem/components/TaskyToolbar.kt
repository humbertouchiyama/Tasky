@file:OptIn(ExperimentalMaterial3Api::class)

package com.humberto.tasky.core.presentation.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.humberto.tasky.core.presentation.designsystem.CrossIcon
import com.humberto.tasky.core.presentation.designsystem.Inter
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme

@Composable
fun TaskyToolbar(
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = {},
    navigationIcon: (@Composable () -> Unit)? = {},
    endContent: (@Composable () -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
) {
    TopAppBar(
        title = {
            title?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    it.invoke()
                }
            }
        },
        modifier = modifier
            .fillMaxWidth(),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            navigationIcon?.let {
                Box(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    it.invoke()
                }
            }
        },
        actions = {
            endContent?.let {
                Box(
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    it.invoke()
                }

            }
        }
    )
    
}

@Preview
@Composable
private fun TaskyToolbarPreview() {
    TaskyTheme {
        TaskyToolbar(
            title = {
                Text(
                    text = "01 MARCH 2022",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 18.sp,
                    fontFamily = Inter
                )
            },
            navigationIcon = {
                Icon(
                    imageVector = CrossIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(20.dp)
                    )
            },
            endContent = {
                Box {
                    Text(
                        text = "Save",
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        )
    }
}