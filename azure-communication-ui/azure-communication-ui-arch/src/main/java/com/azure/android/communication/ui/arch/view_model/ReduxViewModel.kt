package com.azure.android.communication.ui.arch.view_model

import android.os.Handler
import android.os.Looper
import com.azure.android.communication.ui.arch.redux.GenericStore
import com.azure.android.communication.ui.arch.redux.StoreListener

class ReduxViewModel<M : Any>(
    val builder: (store: GenericStore) -> M,
    val onChanged: (viewModel: M) -> Unit,
    private val genericStore: GenericStore
) {
    lateinit var viewModel: M

    private val storeListener = StoreListener {
        rebuild(genericStore)
    }

    private fun rebuild(store: GenericStore) {
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
