// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.arch.redux

import java.util.*
import java.util.concurrent.Executors


enum class AppStoreThreadingMode {
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

class AppStore<S>(
    initialState: S,
    private val reducer: Reducer<S>,
    middlewares: MutableList<Middleware<S>>,
    val threadingMode: AppStoreThreadingMode = AppStoreThreadingMode.Threaded
) : Store<S> {

    private val reductionExecutor = Executors.newSingleThreadExecutor()
    private val callbackExecutor = Executors.newFixedThreadPool(4)
    private var state:S = initialState

    //private val stateFlow = MutableStateFlow(initialState)
    private var middlewareMap: List<(Dispatch) -> Dispatch> =
        middlewares.map { m -> m.invoke(this) }

    private var middlewareDispatch = compose(middlewareMap)(::reduce)
    //private var handler: Handler = storeHandlerThread.startHandlerThread()

    private val listeners : ArrayList<StoreListener<S>> = ArrayList()


    override fun end() {
        //storeHandlerThread.stopHandlerThread()
        middlewareMap = emptyList()
        middlewareDispatch = compose(middlewareMap)(::reduce)
    }

    override fun dispatch(action: Any) {
        if (threadingMode == AppStoreThreadingMode.Immediate) {
            /// Execute from same thread
            middlewareDispatch(action)
            notifyListeners()
        }
        else {
            /// Execute from processing thread
            reductionExecutor.submit {
                middlewareDispatch(action)
                notifyListeners()
            }

        }
    }

    private fun notifyListeners() {
        // Notify the listeners
        listeners.forEach {
            if (threadingMode == AppStoreThreadingMode.Immediate) {
                it.onStoreChanged(getCurrentState())
            }
            else {
                /// Execute from processing thread
                callbackExecutor.submit {
                    it.onStoreChanged(getCurrentState())
                }
            }

        }
    }

    override fun getCurrentState(): S {
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

    override fun addListener(listener: StoreListener<S>) {
        listeners.add(listener)

    }

    override fun removeListener(listener: StoreListener<S>) {
        listeners.remove(listener)
    }
}
