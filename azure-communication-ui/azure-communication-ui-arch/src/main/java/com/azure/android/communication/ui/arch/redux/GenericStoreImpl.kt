// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.arch.redux

import android.util.Log
import java.util.concurrent.Executors

enum class GenericStoreThreadingMode {
    Immediate,
    Threaded,
}

// App Store
//
// Changed to be more Pojo and adjusted threading.
//
// When in "Immediate" mode everything will be run Sync. Nothing will be threaded.
//
// When in "Threaded" mode
//   - Reduction uses a SingleThreadExecutor (will reduce sequentially)
//   - Listeners will be executed in a FixedThreadPool
//
//  In Testing, Immediate Mode simplifies testing your actions/reducers etc, as you don't
//  have to worry about threading.
//
//  In Live mode, threading is used to not block the UI thread.
//
//  Generally after the store is updated, ViewModels should be generated, and then those ViewModels
//  posted in a way that can acted on in the UI Thread.
//
class GenericStoreImpl(
    initialState: GenericState,
    private val reducer: Reducer<GenericState>,
    middlewares: MutableList<Middleware>,
    val threadingMode: GenericStoreThreadingMode = GenericStoreThreadingMode.Threaded
) : GenericStore {

    private val reductionExecutor = Executors.newSingleThreadExecutor()
    private val callbackExecutor = Executors.newFixedThreadPool(4)
    private var state: GenericState = initialState
    private var middlewareMap: List<(Dispatch) -> Dispatch> =
        middlewares.map { m: Middleware -> m.invoke(this) }

    private var middlewareDispatch = compose(middlewareMap)(::reduce)

    private val listeners: ArrayList<StoreListener> = ArrayList()

    override fun end() {
        middlewareMap = emptyList()
        middlewareDispatch = compose(middlewareMap)(::reduce)
    }

    override fun dispatch(action: Any) {
        Log.i("Dispatching:", action.toString())
        if (threadingMode == GenericStoreThreadingMode.Immediate) {
            middlewareDispatch(action)
            notifyListeners()
        } else {
            // / Execute from processing thread
            reductionExecutor.submit {
                middlewareDispatch(action)
                notifyListeners()
            }
        }
    }

    private fun notifyListeners() {
        // Notify the listeners
        listeners.forEach {
            if (threadingMode == GenericStoreThreadingMode.Immediate) {
                it.onStoreChanged(getCurrentState())
            } else {
                // / Execute from processing thread
                callbackExecutor.submit {
                    it.onStoreChanged(getCurrentState())
                }
            }
        }
    }

    override fun getCurrentState(): GenericState {
        return state
    }

    private fun reduce(action: Any) {
        state = reducer.reduce(state, action)
    }

    private fun compose(functions: List<(Dispatch) -> Dispatch>): (Dispatch) -> Dispatch =
        { dispatch ->
            functions.foldRight(
                dispatch
            ) { nextDispatch, composed -> nextDispatch(composed) }
        }

    override fun addListener(listener: StoreListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: StoreListener) {
        listeners.remove(listener)
    }
}
