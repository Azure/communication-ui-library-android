// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

import com.azure.android.communication.ui.calling.presentation.fragment.calling.support.SupportViewModel
import com.azure.android.communication.ui.calling.redux.Dispatch
import com.azure.android.communication.ui.calling.redux.action.NavigationAction
import com.azure.android.communication.ui.calling.redux.state.NavigationState
import com.azure.android.communication.ui.calling.redux.state.NavigationStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
class SupportViewModelTest {

    private lateinit var viewModel: SupportViewModel
    private lateinit var dispatch: Dispatch
    private lateinit var onSubmit: (String, Boolean) -> Unit

    @Before
    fun setUp() {
        dispatch = mock(Function1::class.java) as (Any) -> Unit
        onSubmit = mock(Function2::class.java) as (String, Boolean) -> Unit
        viewModel = SupportViewModel(dispatch, onSubmit)
    }

    @Test
    fun testInit() = runBlockingTest {
        val navigationState = NavigationState(navigationState = NavigationStatus.NONE, supportVisible = true)
        viewModel.init(navigationState)
        assert(viewModel.isVisibleStateFlow.first())
    }

    @Test
    fun testUpdate() = runBlockingTest {
        val navigationState = NavigationState(navigationState = NavigationStatus.NONE, supportVisible = true)
        viewModel.update(navigationState)
        assert(viewModel.isVisibleStateFlow.first())
    }

    @Test
    fun testDismissForm() = runBlockingTest {
        viewModel.dismissForm()
        assert(!viewModel.isVisible)
        verify(dispatch).invoke(NavigationAction.HideSupportForm())
        assert(viewModel.userMessage == "")
    }

    @Test
    fun testSubmit() = runBlockingTest {
        viewModel.userMessage = "Test Message"
        viewModel.forwardEventToUser()
        verify(onSubmit).invoke("Test Message", viewModel.shouldIncludeScreenshot.value)
    }

    @Test
    fun testUserMessage() = runBlockingTest {
        viewModel.userMessage = "New Message"
        assert(viewModel.userMessage == "New Message")
        assert(viewModel.isSubmitEnabledStateFlow.first())
    }

    @Test
    fun testVisibilityToggle() = runBlockingTest {
        viewModel.isVisible = true
        assert(viewModel.isVisibleStateFlow.first())
        viewModel.isVisible = false
        assert(!viewModel.isVisibleStateFlow.first())
    }
}
