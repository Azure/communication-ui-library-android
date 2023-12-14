package com.azure.android.communication.ui.calling.presentation.fragment.calling.support

import com.azure.android.communication.ui.calling.redux.Dispatch
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.NavigationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class SupportViewModel(private val dispatch: Dispatch, private val onSubmit:(String, Boolean)->Unit) {

    private var _isVisibleStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var _userTextStateFlow: MutableStateFlow<String> = MutableStateFlow("")
    var userText get() = _userTextStateFlow.value
        set(value) {
            _userTextStateFlow.value = value
        }

    val isVisibleStateFlow get() = _isVisibleStateFlow as StateFlow<Boolean>

    var isVisible get() = _isVisibleStateFlow.value
        set(value) {
            _isVisibleStateFlow.value = value
        }

    fun init(navigationState: NavigationState) {
        _isVisibleStateFlow = MutableStateFlow(navigationState.supportVisible)
    }

    fun update(navigationState: NavigationState) {
        _isVisibleStateFlow.value = navigationState.supportVisible
    }

    fun dismissForm() {
        dispatch(NavigationAction.HideSupportForm())
        userText = ""
    }

    fun forwardEventToUser() {
        onSubmit(userText, false)
    }
}