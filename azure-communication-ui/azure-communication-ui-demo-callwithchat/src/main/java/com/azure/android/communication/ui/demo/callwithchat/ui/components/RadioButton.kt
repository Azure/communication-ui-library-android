@file:OptIn(ExperimentalMaterial3Api::class)

package com.azure.android.communication.ui.demo.callwithchat.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RadioButtonComponent(label: String, selectedState: MutableState<String>) {
    Row {
        LauncherRadioButton(label, selectedState)
        UserInputSquareField(label = label, enabled = selectedState.value == label) { selectedState.value = label }
    }
}

@Composable
private fun LauncherRadioButton(label: String, selectedState: MutableState<String>) {
    RadioButton(
        modifier = Modifier.padding(top = 8.dp),
        selected = selectedState.value == label,
        onClick = { selectedState.value = label },
    )
}

@Composable
fun RadioGroupTwoButtonColumn(button1Label: String, button2Label: String) {
    val selectedState = rememberSaveable { mutableStateOf(button2Label) }

    RadioButtonComponent(label = button1Label, selectedState = selectedState)
    Spacer(Modifier.size(10.dp))

    RadioButtonComponent(label = button2Label, selectedState = selectedState)
}

@Composable
fun RadioGroupTwoButtonRow(button1Label: String, button2Label: String) {
    val selectedState = rememberSaveable { mutableStateOf("") }
    val selectedUrl = rememberSaveable { mutableStateOf("") }

    Row {
        LauncherRadioButton(button1Label, selectedState)
        Text(modifier = Modifier.padding(top = 8.dp), text = button1Label)

        Spacer(Modifier.size(10.dp))
        LauncherRadioButton(button2Label, selectedState)
        Text(modifier = Modifier.padding(top = 8.dp), text = button2Label)
    }
    Spacer(Modifier.height(8.dp))
    UserInputSquareField(label = "url", enabled = true) {
        selectedState.value = selectedState.value
        selectedUrl.value = it
    }
}
