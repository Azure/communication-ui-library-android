package com.azure.android.communication.ui.calling.presentation.fragment.calling.support

import com.azure.android.communication.ui.calling.redux.Dispatch
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.NavigationState
import com.azure.android.communication.ui.calling.redux.state.NavigationStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class SupportViewModelTest {

    private lateinit var viewModel: SupportViewModel
    private lateinit var dispatch: Dispatch
    private lateinit var onSubmit: (String) -> Unit

    @Before
    fun setUp() {
        dispatch = mock()
        onSubmit = mock()
        viewModel = SupportViewModel(dispatch, onSubmit)
    }

    @Test
    fun `isVisible should update _isVisibleStateFlow`() {
        viewModel.isVisible = true
        assert(viewModel.isVisibleStateFlow.value)
    }

    @Test
    fun `init should set visibility based on navigation state`() {
        val navigationState = NavigationState(NavigationStatus.IN_CALL, supportVisible = true)
        viewModel.init(navigationState)
        assert(viewModel.isVisibleStateFlow.value)
    }

    @Test
    fun `update should clear user message and update visibility when support becomes visible`() {
        val navigationState = NavigationState(NavigationStatus.IN_CALL, supportVisible = true)
        viewModel.update(navigationState)
        assertEquals("", viewModel.userMessage)
        assert(viewModel.isVisibleStateFlow.value)
    }

    @Test
    fun `update should not clear user message when support remains invisible`() {
        viewModel.userMessage = "test"
        val navigationState = NavigationState(NavigationStatus.IN_CALL, supportVisible = false)
        viewModel.update(navigationState)
        assertEquals("test", viewModel.userMessage)
        assert(!viewModel.isVisibleStateFlow.value)
    }

    @Test
    fun `dismissForm should dispatch HideSupportForm action and clear user message`() = runTest {
        viewModel.dismissForm()
        verify(dispatch).invoke(NavigationAction.HideSupportForm())
        assertEquals("", viewModel.userMessage)
    }

    @Test
    fun `userMessage should enable submit when not empty`() {
        viewModel.userMessage = "test"
        assertEquals("test", viewModel.userMessage)
        assert(viewModel.isSubmitEnabledStateFlow.value)
    }

    @Test
    fun `userMessage should disable submit when empty`() {
        viewModel.userMessage = ""
        assertEquals("", viewModel.userMessage)
        assert(!viewModel.isSubmitEnabledStateFlow.value)
    }

    @Test
    fun `init should set isVisible to the value of supportVisible from NavigationState`() {
        val navigationState = NavigationState(NavigationStatus.IN_CALL, supportVisible = true)
        viewModel.init(navigationState)
        assertEquals(true, viewModel.isVisible)
    }

    @Test
    fun `clearEditTextStateFlow should update when support becomes visible`() {
        val previousValue = viewModel.clearEditTextStateFlow.value
        val navigationState = NavigationState(NavigationStatus.IN_CALL, supportVisible = true)
        viewModel.update(navigationState)
        assert(viewModel.clearEditTextStateFlow.value > previousValue)
    }
}
