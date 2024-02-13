// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.support

import com.azure.android.communication.ui.calling.redux.Dispatch
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.NavigationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class SupportViewModel(private val dispatch: Dispatch, private val onSubmit: (String) -> Unit) {

    private var _isVisibleStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _isSubmitEnabledStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var _userMessageStateFlow = MutableStateFlow<String>("")
    private val _clearEditTextStateFlow = MutableStateFlow<Long>(0)

    val clearEditTextStateFlow get() = _clearEditTextStateFlow as StateFlow<Long>

    var userMessage: String get() = _userMessageStateFlow.value
        set(value) {
            _userMessageStateFlow.value = value
            _isSubmitEnabledStateFlow.value = value.isNotEmpty()
        }

    val isVisibleStateFlow get() = _isVisibleStateFlow as StateFlow<Boolean>

    val isSubmitEnabledStateFlow get() = _isSubmitEnabledStateFlow as StateFlow<Boolean>

    var isVisible get() = _isVisibleStateFlow.value
        set(value) {
            _isVisibleStateFlow.value = value
        }

    fun init(navigationState: NavigationState) {
        _isVisibleStateFlow = MutableStateFlow(navigationState.supportVisible)
    }

    fun update(navigationState: NavigationState) {
        if (navigationState.supportVisible && !_isVisibleStateFlow.value) {
            // Made visible, lets trigger a clear of the EditText
            _clearEditTextStateFlow.value = System.currentTimeMillis()
            _userMessageStateFlow.value = ""
        }
        _isVisibleStateFlow.value = navigationState.supportVisible
    }

    fun dismissForm() {
        dispatch(NavigationAction.HideSupportForm())
        _userMessageStateFlow.value = ""
    }

    fun forwardEventToUser() {
        onSubmit(userMessage)
    }
}
