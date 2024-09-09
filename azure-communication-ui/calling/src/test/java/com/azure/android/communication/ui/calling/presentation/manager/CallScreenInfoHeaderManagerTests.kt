// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <CUSTOM_CALL_HEADER> */
package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenHeaderViewData
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenOptions
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.CallScreenInfoHeaderAction
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
internal class CallScreenInfoHeaderManagerTests : ACSBaseTestCoroutine() {
    @Test
    fun callScreenInformationHeaderManager_updateTitle_action() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }

        val callScreenHeaderViewData = CallCompositeCallScreenHeaderViewData().setTitle("initial title")
        val config = CallCompositeConfiguration()
        config.callScreenOptions = CallCompositeCallScreenOptions()
        config.callScreenOptions?.headerViewData = callScreenHeaderViewData

        val manager = UpdatableOptionsManager(config, mockAppStore)
        manager.start()

        val title = "title"

        // Act
        callScreenHeaderViewData.setTitle(title)

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is CallScreenInfoHeaderAction.UpdateTitle &&
                    action.title == title
            }
        )
    }

    @Test
    fun callScreenInformationHeaderManager_updateSubtitle_action() {
        // Arrange
        val mockAppStore = mock<AppStore<ReduxState>> {
            on { dispatch(any()) } doAnswer { }
        }

        val callScreenHeaderViewData = CallCompositeCallScreenHeaderViewData().setSubtitle("initial subtitle")
        val config = CallCompositeConfiguration()
        config.callScreenOptions = CallCompositeCallScreenOptions()
        config.callScreenOptions?.headerViewData = callScreenHeaderViewData

        val manager = UpdatableOptionsManager(config, mockAppStore)
        manager.start()

        val subtitle = "subtitle"

        // Act
        callScreenHeaderViewData.setSubtitle(subtitle)

        // Assert
        verify(mockAppStore, times(1)).dispatch(
            argThat { action ->
                action is CallScreenInfoHeaderAction.UpdateSubtitle &&
                    action.subtitle == subtitle
            }
        )
    }
}
/* </CUSTOM_CALL_HEADER> */
