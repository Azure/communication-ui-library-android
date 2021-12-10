// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.setup.components

import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.redux.state.ReduxState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class PreviewAreaViewViewModelUnitTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun rendererForegroundViewViewModel_switchCamera_then_dispatchCameraSwitchTriggered() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }
        val rendererForegroundViewViewModel =
            PreviewAreaViewModel(mockAppStore::dispatch)

        // Act
        rendererForegroundViewViewModel.switchCamera()

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LocalParticipantAction.CameraSwitchTriggered
            }
        )
    }
}
