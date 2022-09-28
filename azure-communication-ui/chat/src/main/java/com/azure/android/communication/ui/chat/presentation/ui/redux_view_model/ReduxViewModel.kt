package com.azure.android.communication.ui.chat.presentation.ui.redux_view_model

import androidx.lifecycle.LifecycleCoroutineScope
import com.azure.android.communication.ui.chat.redux.AppStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class ReduxViewModel<T, M : Any>(
    val builder: (store: AppStore<T>) -> M,
    val onChanged: (viewModel: M) -> Unit,
    private val coroutineScope: LifecycleCoroutineScope,
    private val store: AppStore<T>
) {
    lateinit var viewModel: M

    private fun rebuild(store: AppStore<T>) {
        val newState = builder(store)

        if (!this::viewModel.isInitialized) {
            viewModel = newState
            postOnChanged()
            return
        } else if (newState != viewModel) {
            viewModel = newState
            postOnChanged()
        }
    }

    private fun postOnChanged() {
        coroutineScope.launch(Dispatchers.Main) {
            onChanged(viewModel)
        }
    }

    fun start() {
        coroutineScope.launch(Dispatchers.Default) {
            store.getStateFlow().collect {
                rebuild(store)
            }
        }
    }
}