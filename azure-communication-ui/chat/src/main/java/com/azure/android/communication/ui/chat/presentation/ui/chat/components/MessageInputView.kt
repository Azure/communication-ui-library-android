// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.ui.chat.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.R
import com.azure.android.communication.ui.chat.presentation.style.ChatCompositeTheme
import com.azure.android.communication.ui.chat.presentation.ui.chat.UITestTags
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.action.ChatAction

@Composable
internal fun MessageInputView(
    contentDescription: String,
    messageInputTextState: MutableState<String>,
    postAction: (Action) -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions(),
    sendMessageEnabled: Boolean,
) {
    var focusState by rememberSaveable { mutableStateOf(false) }

    MessageInput(
        onTextChanged = {
            messageInputTextState.value = it
            if (sendMessageEnabled) {
                postAction(ChatAction.TypingIndicator())
            }
        },
        textContent = messageInputTextState.value,
        onTextFieldFocused = { focusState = it },
        focusState = focusState,
        contentDescription = contentDescription,
        keyboardActions = keyboardActions,
    )
}

@Composable
internal fun MessageInput(
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (String) -> Unit,
    textContent: String,
    onTextFieldFocused: (Boolean) -> Unit,
    focusState: Boolean,
    contentDescription: String,
    keyboardActions: KeyboardActions,
) {
    val outlineColor = ChatCompositeTheme.colors.outlineColor
    val textColor = ChatCompositeTheme.colors.textColor

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val maxInputHeight = screenHeight / 4

    val semantics =
        Modifier.semantics {
            this.contentDescription = contentDescription
        }

    BasicTextField(
        modifier =
            Modifier
                .fillMaxWidth(fraction = 0.9f)
                .padding(6.dp)
                .heightIn(40.dp, maxInputHeight)
                .onFocusChanged { onTextFieldFocused(it.isFocused) }
                .testTag(UITestTags.MESSAGE_INPUT_BOX)
                .then(semantics),
        value = textContent,
        onValueChange = { onTextChanged(it) },
        textStyle =
            TextStyle(
                color = textColor,
            ),
        singleLine = false,
        keyboardActions = keyboardActions,
        cursorBrush = SolidColor(ChatCompositeTheme.colors.unreadMessageIndicatorBackground),
        decorationBox = { innerTextField ->
            Box(
                modifier =
                    Modifier
                        .border(1.dp, outlineColor, RoundedCornerShape(10))
                        .padding(9.dp, 6.dp, 9.dp, 6.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (textContent.isEmpty() && !focusState) {
                    BasicText(
                        text = stringResource(R.string.azure_communication_ui_chat_enter_a_message),
                        style =
                            TextStyle(
                                color = textColor,
                            ),
                    )
                }

                innerTextField()
            }
        },
    )
}

@Preview
@Composable
internal fun PreviewMessageInputView() {
    MessageInputView("Message Input Field", remember { mutableStateOf("") }, {}, sendMessageEnabled = true)
}
