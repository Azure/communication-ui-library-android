// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux

import android.os.Handler
import android.os.Looper
import com.azure.android.communication.ui.calling.CallCompositeException
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.reducer.Reducer
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal class AppStore<S>(
    initialState: S,
    private val reducer: Reducer<S>,
    middlewares: MutableList<Middleware<S>>,
    dispatcher: CoroutineContext,
) : Store<S> {
    // Any exceptions encountered in the reducer are rethrown to crash the app and not get silently ignored.
    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Handler(Looper.getMainLooper()).postAtFrontOfQueue {
                throw CallCompositeException("App store exception while reducing state", throwable)
            }
            // At this point (after an exception) we don't want to accept any more work.
            scope.cancel()
        }
    private val dispatcherWithExceptionHandler = dispatcher + exceptionHandler
    private val scope = CoroutineScope(dispatcher)
    private val stateFlow = MutableStateFlow(initialState)
    private var middlewareMap: List<(Dispatch) -> Dispatch> =
        middlewares.map { m -> m.invoke(this) }

    private var middlewareDispatch = compose(middlewareMap)(::reduce)

    override fun end() {
        scope.cancel()
        middlewareMap = emptyList()
        middlewareDispatch = compose(middlewareMap)(::reduce)
    }

    override fun dispatch(action: Action) {
        scope.launch(dispatcherWithExceptionHandler) {
            middlewareDispatch(action)
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
            ) { nextDispatch, composed -> nextDispatch(composed) }
        }
}
