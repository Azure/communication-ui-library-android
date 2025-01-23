// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.captions

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.calling.presentation.manager.CaptionsRttDataManager
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
import com.azure.android.communication.ui.calling.redux.state.DeviceConfigurationState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.redux.state.RttState
import com.azure.android.communication.ui.calling.service.CallingService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class CaptionsLayoutViewModelUnitTest : ACSBaseTestCoroutine() {

    @Mock
    private lateinit var callingService: CallingService
    @Mock
    private lateinit var appStore: AppStore<ReduxState>

    @Mock
    private lateinit var avatarViewManager: AvatarViewManager

    private lateinit var captionsRttDataManager: CaptionsRttDataManager

    private lateinit var deviceConfigurationState: DeviceConfigurationState

    @Before
    fun setUp() {
        deviceConfigurationState = DeviceConfigurationState(
            isSoftwareKeyboardVisible = false,
            isTablet = false,
            isPortrait = false,
        )
        captionsRttDataManager = CaptionsRttDataManager(
            callingService,
            appStore,
            avatarViewManager,
            null,
            null,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsLinearLayoutViewModelUnitTest_update_then_notShowCaptions() {
        runScopedTest {

            // arrange
            val viewModel = buildCaptionsViewModel()

            val resultDisplayErrorHeaderStateFlow =
                mutableListOf<Boolean?>()

            viewModel.init(
                CaptionsState(isCaptionsUIEnabled = false, status = CaptionsStatus.NONE),
                rttState = RttState(),
                isVisible = true,
                deviceConfigurationState,
            )

            val displayErrorJob = launch {
                viewModel.isVisibleFlow
                    .toList(resultDisplayErrorHeaderStateFlow)
            }

            val rttState = RttState()
            var isVisible = true
            val deviceConfigurationState = DeviceConfigurationState()

            // act
            viewModel.update(
                CaptionsState(isCaptionsUIEnabled = false, status = CaptionsStatus.NONE),
                rttState,
                isVisible,
                deviceConfigurationState,
            )

            isVisible = false
            viewModel.update(
                CaptionsState(isCaptionsUIEnabled = false, status = CaptionsStatus.NONE),
                rttState,
                isVisible,
                deviceConfigurationState,
            )

            // assert
            Assert.assertEquals(
                true,
                resultDisplayErrorHeaderStateFlow[0]
            )

            Assert.assertEquals(
                false,
                resultDisplayErrorHeaderStateFlow[1]
            )

            Assert.assertEquals(
                2,
                resultDisplayErrorHeaderStateFlow.count()
            )

            displayErrorJob.cancel()
        }
    }

    private fun buildCaptionsViewModel(): CaptionsViewModel {
        return CaptionsViewModel({}, captionsRttDataManager)
    }
}
