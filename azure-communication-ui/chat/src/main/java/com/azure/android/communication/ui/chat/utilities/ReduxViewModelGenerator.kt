// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.utilities

import com.azure.android.communication.ui.chat.redux.AppStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Redux View Model
 *
 * Listens to the Store and Emits ViewModels
 *
 *  T = Type of the Store store
 *  M = Type of the ViewModel

 * Params:
 *   builder: (T) -> M
 *   onChanged: (M) -> Unit
 *   coroutineScope: The lifecycle scope to run this under
 *   store: The Store you want to listen to
 */
internal class ReduxViewModelGenerator<T, M : Any>(
    val builder: (store: AppStore<T>) -> M,
    val onChanged: (viewModel: M) -> Unit,
    private val coroutineScope: CoroutineScope,
    private val store: AppStore<T>,
) {
    private var storeListeningJob: Job
    lateinit var viewModel: M

    init {
        storeListeningJob =
            coroutineScope.launch(Dispatchers.Default) {
                store.getStateFlow().collect {
                    rebuild(store)
                }
            }
    }

    fun stop() {
        storeListeningJob.cancel()
    }

    // Rebuild the View Model
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

    // Post the onChanged notification on the main thread
    private fun postOnChanged() {
        coroutineScope.launch(Dispatchers.Main) {
            onChanged(viewModel)
        }
    }
}
