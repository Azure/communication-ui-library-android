// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.redux

import android.os.Handler
import com.azure.android.communication.ui.redux.action.Action
import com.azure.android.communication.ui.redux.reducer.Reducer
import com.azure.android.communication.ui.utilities.StoreHandlerThread
import kotlinx.coroutines.flow.MutableStateFlow

internal class AppStore<S>(
    initialState: S,
    private val reducer: Reducer<S>,
    middlewares: MutableList<Middleware<S>>,
    private val storeHandlerThread: StoreHandlerThread,
) : Store<S> {

    private val stateFlow = MutableStateFlow(initialState)
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

    override fun getStateFlow(): MutableStateFlow<S> {
        return stateFlow
    }

    override fun getCurrentState(): S {
        return stateFlow.value
    }

    private fun reduce(action: Action) {
        stateFlow.value = reducer.reduce(stateFlow.value, action)
    }

    private fun compose(functions: List<(Dispatch) -> Dispatch>): (Dispatch) -> Dispatch =
        { dispatch ->
            functions.foldRight(
                dispatch,
                { nextDispatch, composed -> nextDispatch(composed) }
            )
        }
}
