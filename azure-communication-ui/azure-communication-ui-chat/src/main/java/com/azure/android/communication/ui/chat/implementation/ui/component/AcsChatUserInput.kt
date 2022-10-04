package com.azure.android.communication.ui.chat.implementation.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.azure.android.communication.ui.chat.R

enum class AcsChatInputSelector {
    NONE, ATTACH
}

@Preview
@Composable
fun AcsChatUserInputPreview() {
    AcsChatUserInput(onMessageSent = {}, onUserTyping = {})
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AcsChatUserInput(
    onMessageSent: (String) -> Unit,
    onUserTyping: (String) -> Unit,
    modifier: Modifier = Modifier,
    resetScroll: () -> Unit = {},
) {
    var currentInputSelector by rememberSaveable { mutableStateOf(AcsChatInputSelector.NONE) }
    val dismissKeyboard = { currentInputSelector = AcsChatInputSelector.NONE }

    var textState by remember { mutableStateOf(TextFieldValue()) }

    var textFieldFocusState by remember { mutableStateOf(false) }
    Surface(
        tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            AcsChatInputSelectorButton(
                onClick = { },
                icon = painterResource(id = R.drawable.azure_communication_ui_chat_ic_attach_20_filled),
                selected = currentInputSelector == AcsChatInputSelector.ATTACH,
                description = "Attach Button"
            )
            AcsChatUserInputText(
                textFieldValue = textState,
                onTextChanged = { textState = it; onUserTyping(it.text) },
                keyboardShown = currentInputSelector == AcsChatInputSelector.NONE && textFieldFocusState,
                onTextFieldFocused = { focused ->
                    if (focused) {
                        currentInputSelector = AcsChatInputSelector.NONE
                        resetScroll()
                    }
                    textFieldFocusState = focused
                },

                focusState = textFieldFocusState,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = {
                onMessageSent(textState.text)
                textState = TextFieldValue()
                resetScroll()
                dismissKeyboard()
            }, modifier = Modifier.size(48.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.azure_communication_ui_chat_ic_send_20_filled),
                    tint = Color("#0078D4".toColorInt()),
                    modifier = Modifier.padding(14.dp),
                    contentDescription = "Send Button"
                )
            }
            }
        }
    }

    @Composable
    private fun AcsChatInputSelectorButton(
        onClick: () -> Unit,
        icon: Painter,
        description: String,
        selected: Boolean
    ) {
        IconButton(
            onClick = onClick, modifier = Modifier.size(48.dp)
        ) {
            val tint = if (selected) {
                MaterialTheme.colorScheme.onSecondary
            } else {
                MaterialTheme.colorScheme.secondary
            }
            Icon(
                icon, tint = tint, modifier = Modifier.padding(14.dp), contentDescription = description
            )
        }
    }

    val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
    var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

    @ExperimentalFoundationApi
    @Composable
    private fun AcsChatUserInputText(
        keyboardType: KeyboardType = KeyboardType.Text,
        onTextChanged: (TextFieldValue) -> Unit,
        textFieldValue: TextFieldValue,
        keyboardShown: Boolean,
        onTextFieldFocused: (Boolean) -> Unit,
        focusState: Boolean,
        modifier: Modifier
    ) {
        Row(
            modifier = modifier.then(
                Modifier
                    .height(64.dp)
                    .padding(5.dp)
                    .border(
                        width = 0.8.dp,
                        color = Color("#E1E1E1".toColorInt()),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .semantics {
                        keyboardShownProperty = keyboardShown
                    }
            ),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Surface {
                Box(
                    modifier = Modifier
                        .height(64.dp)
                        .background(Color.White)
                        .padding(end = 50.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    var lastFocusState by remember { mutableStateOf(false) }
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { onTextChanged(it) },
                        modifier = Modifier
                            .padding(start = 32.dp)
                            .align(Alignment.CenterStart)
                            .onFocusChanged { state ->
                                if (lastFocusState != state.isFocused) {
                                    onTextFieldFocused(state.isFocused)
                                }
                                lastFocusState = state.isFocused
                            },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = keyboardType, imeAction = ImeAction.Send
                        ),
                        maxLines = 1,
                        cursorBrush = SolidColor(LocalContentColor.current),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
                    )

                    val disableContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    if (textFieldValue.text.isEmpty() && !focusState) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 5.dp),
                            text = "Enter your message...",
                            style = MaterialTheme.typography.bodyLarge.copy(color = disableContentColor)
                        )
                    }
                }
            }
        }
    }
    