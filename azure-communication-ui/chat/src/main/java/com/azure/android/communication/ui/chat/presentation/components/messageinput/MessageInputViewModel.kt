package com.azure.android.communication.ui.chat.presentation.components.messageinput

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MessageInputViewModel : ViewModel() {

    private val mTextState: MutableLiveData<TextFieldValue> = MutableLiveData(TextFieldValue())
    private val mFocusState: MutableLiveData<Boolean> = MutableLiveData(true)

    val textState: LiveData<TextFieldValue> = mTextState
    val focusState: LiveData<Boolean> = mFocusState

    fun onTextStateChange(newTextState: TextFieldValue) {
        mTextState.value = newTextState
    }
    fun onFocusChange(newFocusState: Boolean) {
        mFocusState.value = newFocusState
    }
}
