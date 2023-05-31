package com.azure.android.communication.ui.calling.presentation.fragment.factories

import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.ErrorInfoViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.PermissionWarningViewModel
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.state.ReduxState

internal open class BaseViewModelFactory constructor(
    private val store: Store<ReduxState>,
) {
    val warningsViewModel by lazy {
        PermissionWarningViewModel(store::dispatch)
    }

    val errorInfoViewModel by lazy {
        ErrorInfoViewModel()
    }
}
