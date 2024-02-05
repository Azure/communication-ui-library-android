// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.presentation.manager.LifecycleManagerImpl
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.LifecycleAction
import com.azure.android.communication.ui.calling.redux.state.ReduxState
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
internal class LifeCycleManagerUnitTests {
    @Test
    fun lifecycleManager_pause_then_sendEnterBackgroundTriggered() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }
        val lifecycleManagerImpl = LifecycleManagerImpl(mockAppStore)

        // Act
        lifecycleManagerImpl.pause()

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LifecycleAction.EnterBackgroundTriggered
            }
        )
    }

    @Test
    fun lifecycleManager_resume_then_sendEnterForegroundTriggered() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }
        val lifecycleManagerImpl = LifecycleManagerImpl(mockAppStore)

        // Act
        lifecycleManagerImpl.resume()

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is LifecycleAction.EnterForegroundTriggered
            }
        )
    }
}
