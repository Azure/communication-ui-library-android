// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.notification

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.MediaCallDiagnostic
import com.azure.android.communication.ui.calling.models.UpperMessageBarNotificationModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.UpperMessageBarNotificationViewModel
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.action.CallDiagnosticsAction
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class UpperMessageBarNotificationViewModelUnitTest : ACSBaseTestCoroutine() {

    @ExperimentalCoroutinesApi
    @Test
    fun upperMessageBarNotificationViewModel_init() {
        runScopedTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val upperMessageBarNotificationModel = UpperMessageBarNotificationModel(
                1,
                2,
                MediaCallDiagnostic.SPEAKER_MUTED
            )
            val upperMessageBarNotificationViewModel = UpperMessageBarNotificationViewModel(
                mockAppStore::dispatch,
                upperMessageBarNotificationModel
            )

            // act
            val resultUpperMessageBarNotificationModelMessageFlow = mutableListOf<UpperMessageBarNotificationModel>()
            val flowJob = launch {
                upperMessageBarNotificationViewModel.getUpperMessageBarNotificationModelFlow().toList(resultUpperMessageBarNotificationModelMessageFlow)
            }

            // assert
            Assert.assertEquals(1, resultUpperMessageBarNotificationModelMessageFlow.last().notificationIconId)
            Assert.assertEquals(2, resultUpperMessageBarNotificationModelMessageFlow.last().notificationMessageId)
            Assert.assertEquals(MediaCallDiagnostic.SPEAKER_MUTED, resultUpperMessageBarNotificationModelMessageFlow.last().mediaCallDiagnostic)

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun upperMessageBarNotificationViewModel_dismissNotificationByUser() {
        runScopedTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val upperMessageBarNotificationModel = UpperMessageBarNotificationModel(
                1,
                2,
                MediaCallDiagnostic.SPEAKER_MUTED
            )
            val upperMessageBarNotificationViewModel = UpperMessageBarNotificationViewModel(
                mockAppStore::dispatch,
                upperMessageBarNotificationModel
            )

            upperMessageBarNotificationViewModel.dismissNotificationByUser()

            verify(mockAppStore, times(1)).dispatch(
                argThat { action ->
                    action is CallDiagnosticsAction.MediaCallDiagnosticsDismissed
                }
            )

            // act
            val resultDismissUpperMessageBarNotificationModelMessageFlow = mutableListOf<Boolean>()
            val flowJob = launch {
                upperMessageBarNotificationViewModel.getDismissUpperMessageBarNotificationFlow().toList(resultDismissUpperMessageBarNotificationModelMessageFlow)
            }

            // assert
            Assert.assertEquals(true, resultDismissUpperMessageBarNotificationModelMessageFlow.last())

            flowJob.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun upperMessageBarNotificationViewModel_dismissNotification() {
        runScopedTest {
            // arrange
            val mockAppStore = mock<AppStore<ReduxState>> {}
            val upperMessageBarNotificationModel = UpperMessageBarNotificationModel(
                1,
                2,
                MediaCallDiagnostic.SPEAKER_MUTED
            )
            val upperMessageBarNotificationViewModel = UpperMessageBarNotificationViewModel(
                mockAppStore::dispatch,
                upperMessageBarNotificationModel
            )

            upperMessageBarNotificationViewModel.dismissNotification()

            // act
            val resultDismissUpperMessageBarNotificationModelMessageFlow = mutableListOf<Boolean>()
            val flowJob = launch {
                upperMessageBarNotificationViewModel.getDismissUpperMessageBarNotificationFlow().toList(resultDismissUpperMessageBarNotificationModelMessageFlow)
            }

            // assert
            Assert.assertEquals(true, resultDismissUpperMessageBarNotificationModelMessageFlow.last())

            flowJob.cancel()
        }
    }
}
