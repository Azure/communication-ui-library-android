// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.arch.redux

import android.os.Handler
//import com.azure.android.communication.ui.arch.redux.action.Action
//import com.azure.android.communication.ui.arch.redux.reducer.Reducer
//import com.azure.android.communication.ui.arch.redux.StoreHandlerThread

internal class AppStore<S>(
    initialState: S,
    private val reducer: Reducer<S>,
    middlewares: MutableList<Middleware<S>>,
) : Store<S> {

    private var state:S = initialState

    //private val stateFlow = MutableStateFlow(initialState)
    private var middlewareMap: List<(Dispatch) -> Dispatch> =
        middlewares.map { m -> m.invoke(this) }

    private var middlewareDispatch = compose(middlewareMap)(::reduce)
    //private var handler: Handler = storeHandlerThread.startHandlerThread()

    override fun end() {
        //storeHandlerThread.stopHandlerThread()
        middlewareMap = emptyList()
        middlewareDispatch = compose(middlewareMap)(::reduce)
    }

    override fun dispatch(action: Any) {
       // if (storeHandlerThread.isHandlerThreadAlive()) {
       //     handler.post {
                middlewareDispatch(action)
       //     }
       // }
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
}
