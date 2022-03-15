// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux

import android.os.Handler
import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.reducer.Reducer
import com.azure.android.communication.ui.utilities.StoreHandlerThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Timer
import java.util.TimerTask

internal class AppStore<S>(
    initialState: S,
    private val reducer: Reducer<S>,
    middlewares: MutableList<Middleware<S>>,
    private val storeHandlerThread: StoreHandlerThread,
) : Store<S> {

    private var debounceTimer: Timer? = null
    private val stateFlow = MutableStateFlow(initialState)
    private val stateFlowInternal = MutableStateFlow(initialState)
    private var middlewareMap: List<(Dispatch) -> Dispatch> =
        middlewares.map { m -> m.invoke(this) }

    private var middlewareDispatch = compose(middlewareMap)(::reduce)
    private var handler: Handler = storeHandlerThread.startHandlerThread()

    override fun end() {
        storeHandlerThread.stopHandlerThread()
        middlewareMap = emptyList()
        middlewareDispatch = compose(middlewareMap)(::reduce)
    }

    override fun dispatch(action: Action) {
        if (storeHandlerThread.isHandlerThreadAlive()) {
            handler.post {
                middlewareDispatch(action)
            }
        }
    }

    override fun getStateFlow(): StateFlow<S> {
        return stateFlow
    }

    override fun getCurrentState(): S {
        return stateFlowInternal.value
    }

    private fun reduce(action: Action) {
        stateFlowInternal.value = reducer.reduce(stateFlowInternal.value, action)
        debouncePostUpdate()
    }

    // Post the update to the StateFlow in a debounced way (to reduce UI updates)

    private fun debouncePostUpdate() {
        debounceTimer?.cancel()
        debounceTimer = Timer()
        debounceTimer?.schedule(
            object : TimerTask() {
                override fun run() {
                    stateFlow.value = stateFlowInternal.value
                    // Done, clear the timer
                    debounceTimer = null
                }
            },
            1
        )
    }

    private fun compose(functions: List<(Dispatch) -> Dispatch>): (Dispatch) -> Dispatch =
        { dispatch ->
            functions.foldRight(
                dispatch,
                { nextDispatch, composed -> nextDispatch(composed) }
            )
        }
}
