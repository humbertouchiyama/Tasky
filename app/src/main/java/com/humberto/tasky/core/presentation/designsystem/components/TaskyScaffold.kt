package com.humberto.tasky.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun TaskyScaffold(
    modifier: Modifier = Modifier,
    topAppBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    title: String? = null,
    showRoundedBordersBackground: Boolean = true,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = topAppBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        modifier = modifier
    ) { innerPadding ->
        if (showRoundedBordersBackground) {
            RoundedBordersBackground(
                title = title,
                modifier = Modifier.padding(innerPadding)
            ) {
                content()
            }
        } else {
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .zIndex(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    content()
                }
            }
        }
    }
}