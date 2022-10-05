package com.azure.android.communication.ui.chat.presentation.ui.chat.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.R

@Preview(showSystemUi = true)
@Composable
fun MessageInputViewPreview() {
    Row {
        MessageInputView(Color(0xFF212121), Color(0xFFE1E1E1), "Message Input Field")
    }
}

@Composable
fun MessageInputView(textColor: Color, outlineColor: Color, contentDescription: String) {
    var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
    var focusState by rememberSaveable { mutableStateOf(false) }

    MessageInput(
        onTextChanged = { textState = it },
        textState = textState,
        onTextFieldFocused = { focusState = it },
        focusState = focusState,
        textColor = textColor,
        outlineColor = outlineColor,
        contentDescription = contentDescription
    )
}

@Composable
fun MessageInput(
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textState: TextFieldValue,
    onTextFieldFocused: (Boolean) -> Unit,
    focusState: Boolean,
    textColor: Color,
    outlineColor: Color,
    contentDescription: String
) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val maxInputHeight = screenHeight / 4

    val semantics = Modifier.semantics {
        this.contentDescription = contentDescription
    }

    BasicTextField(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.9f)
            .padding(6.dp)
            .semantics { semantics }
            .heightIn(52.dp, maxInputHeight)
            .onFocusChanged { onTextFieldFocused(it.isFocused) },
        value = textState,
        onValueChange = { onTextChanged(it) },
        textStyle = androidx.compose.ui.text.TextStyle(
            color = textColor
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Send
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .border(1.dp, outlineColor, RoundedCornerShape(10))
                    .padding(6.dp, 0.dp, 6.dp, 0.dp),
                contentAlignment = Alignment.CenterStart,
            ) {

                if (textState.text.isEmpty() && !focusState) {
                    BasicText(
                        text = stringResource(R.string.azure_communication_ui_call_enter_a_message),
                        style = androidx.compose.ui.text.TextStyle(
                            color = textColor
                        )
                    )
                }

                innerTextField()
            }
        }
    )
}
