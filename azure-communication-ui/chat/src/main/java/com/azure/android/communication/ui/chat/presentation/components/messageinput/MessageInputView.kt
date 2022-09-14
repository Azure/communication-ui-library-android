package com.azure.android.communication.ui.chat.presentation.components.messageinput

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showSystemUi = true)
@Composable
fun MessageInputAreaPreview() {
    MessageInputArea(MessageInputViewModel())
}

@Composable
fun MessageInputArea(messageInputViewModel: MessageInputViewModel) {
    // Message Input Area = message input field + send button

//    val textState: TextFieldValue by messageInputViewModel.textState.observeAsState(TextFieldValue())
//    val focusState: Boolean by messageInputViewModel.focusState.observeAsState(false)

    var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
    var focusState by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        verticalAlignment = CenterVertically

    ) {
        MessageInput(
            onTextChanged = { textState = it },
            textState = textState,
            onTextFieldFocused = { focusState = it },
            focusState = focusState
        )
        // TODO: SendButton()
    }
}

@Composable
fun MessageInput(
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textState: TextFieldValue,
    onTextFieldFocused: (Boolean) -> Unit,
    focusState: Boolean,
) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val maxInputHeight = screenHeight / 4

    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .heightIn(52.dp, maxInputHeight)
            .onFocusChanged { onTextFieldFocused(it.isFocused) },
        value = textState,
        onValueChange = { onTextChanged(it) },
        textStyle = androidx.compose.ui.text.TextStyle(
            color = Color.fromHex("#212121")
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Send
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .border(1.dp, Color.fromHex("#E1E1E1"), RoundedCornerShape(10))
                    .padding(6.dp, 0.dp, 6.dp, 0.dp),
                contentAlignment = Alignment.CenterStart,
            ) {

                if (textState.text.isEmpty() && !focusState) {
                    BasicText(
                        text = "Enter a message",
                        style = androidx.compose.ui.text.TextStyle(
                            color = Color.fromHex("#ACACAC")
                        )
                    )
                }
                innerTextField()
            }
        }
    )
}

private fun Color.Companion.fromHex(color: String) = Color(android.graphics.Color.parseColor(color))
