package com.azure.android.communication.ui.chat.presentation.ui.reduxviewmodelgenerator

import com.azure.android.communication.ui.chat.redux.AppStore
import com.azure.android.communication.ui.chat.redux.action.Action
import com.azure.android.communication.ui.chat.redux.reducer.Reducer
import com.azure.android.communication.ui.chat.utilities.ReduxViewModelGenerator
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher

internal class ReduxViewModelTest : Reducer<BasicState> {
    // @Test not ready yet
    fun testUpdate() {
        var outputModel: BasicViewModel? = null
        val store =
            AppStore<BasicState>(
                initialState = BasicState(0),
                reducer = this@ReduxViewModelTest,
                middlewares = mutableListOf(),
                dispatcher = UnconfinedTestDispatcher(),
            )

        val rvm =
            ReduxViewModelGenerator(
                builder = { store -> BasicViewModel(countAsString = "${store.getCurrentState().count}") },
                store = store,
                onChanged = { viewModel -> outputModel = viewModel },
                coroutineScope = TestScope(UnconfinedTestDispatcher()),
            )
        store.dispatch(Increment())

        assertNotNull(outputModel)
    }

    override fun reduce(
        state: BasicState,
        action: Action,
    ): BasicState {
        when (action) {
            is Increment -> return BasicState(state.count + 1)
        }
        return state
    }
}

data class BasicState(val count: Int)

data class BasicViewModel(val countAsString: String)

class Increment : Action
