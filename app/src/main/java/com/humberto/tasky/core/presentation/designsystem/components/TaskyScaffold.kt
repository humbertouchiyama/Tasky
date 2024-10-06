package com.humberto.tasky.core.presentation.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TaskyScaffold(
    modifier: Modifier = Modifier,
    topAppBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    title: String? = null,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = topAppBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        modifier = modifier
    ) { innerPadding ->
        RoundedBordersBackground(
            title = title,
            modifier = Modifier.padding(innerPadding)
        ) {
            content()
        }
    }
}