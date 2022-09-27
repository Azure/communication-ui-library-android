package com.azure.android.communication.ui.chat.presentation.ui.redux_view_model

import android.os.Handler
import android.os.Looper
import com.azure.android.communication.ui.chat.redux.AppStore

internal class ReduxViewModel<T, M : Any>(
    val builder: (store: AppStore<T>) -> M,
    val onChanged: (viewModel: M) -> Unit,
    private val genericStore: AppStore<T>
) {
    lateinit var viewModel: M

    //private val storeListener = StoreListener {
//        rebuild(genericStore)
//    }

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
        Handler(Looper.getMainLooper()).post {
            onChanged(viewModel)
        }
    }

    fun start() {

        genericStore.addListener(storeListener)
        rebuild(genericStore)
    }

    fun stop() {
        genericStore.removeListener(storeListener)
    }
}