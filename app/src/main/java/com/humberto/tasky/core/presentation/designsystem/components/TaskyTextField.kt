package com.humberto.tasky.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.humberto.tasky.core.presentation.designsystem.TaskyGreen
import com.humberto.tasky.core.presentation.designsystem.TaskyLightBlue
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme

@Composable
fun TaskyTextField(
    state: TextFieldState,
    endIcon: ImageVector?,
    hint: String,
    modifier: Modifier = Modifier,
    hasError: Boolean? = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    isFocused: MutableState<Boolean>,
    focusRequester: FocusRequester
) {
    Column(
        modifier = modifier
    ) {
        BasicTextField(
            state = state,
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
            ),
            lineLimits = TextFieldLineLimits.SingleLine,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .border(
                    width = if(isFocused.value || hasError == true) 1.dp else 0.dp,
                    color = if(isFocused.value) {
                        TaskyLightBlue
                    } else if (hasError == true) {
                        MaterialTheme.colorScheme.error
                    } else {
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 20.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused.value = it.isFocused
                }
                .defaultMinSize(minHeight = 64.dp),
            decorator = { innerBox ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        if (state.text.isEmpty() && !isFocused.value) {
                            Text(
                                text = hint,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = 0.4f
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        innerBox()
                    }
                    if (endIcon != null) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(modifier = Modifier
                            .padding(12.dp)) {
                            Icon(
                                imageVector = endIcon,
                                contentDescription = null,
                                tint = TaskyGreen,
                                modifier = Modifier.size(20.dp)
                            )

                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun TaskyTextFieldPreview() {
    val isFocusedState = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    TaskyTheme {
        TaskyTextField(
            state = rememberTextFieldState(),
            endIcon = Icons.Default.Check,
            hint = "Email address",
            modifier = Modifier
                .fillMaxWidth(),
            isFocused = isFocusedState,
            focusRequester = focusRequester
        )
    }
}