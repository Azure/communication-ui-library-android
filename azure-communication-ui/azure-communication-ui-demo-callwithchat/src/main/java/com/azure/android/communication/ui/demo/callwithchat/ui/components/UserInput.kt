package com.azure.android.communication.ui.demo.callwithchat.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

typealias OnTapListener = (String) -> Unit
@Composable
internal fun UserInputSquareField(label: String, enabled: Boolean, listener: OnTapListener) {
    val content = rememberSaveable { mutableStateOf("") }

    TextField(
        enabled = enabled,
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        value = content.value,
        onValueChange = {
            content.value = it
            listener(it)
        },
        label = { Text(label) },
        placeholder = { Text("Write something") },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrect = true,
            keyboardType = KeyboardType.Text,
        ),
        textStyle = TextStyle(color = Color.Black, fontSize = 15.sp, fontFamily = FontFamily.SansSerif),
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Gray,
            disabledTextColor = Color.Transparent,
            containerColor = Color.Transparent,
            focusedIndicatorColor = Color.DarkGray,
            unfocusedIndicatorColor = Color.DarkGray,
            disabledIndicatorColor = Color.Transparent
        )
    )
}
