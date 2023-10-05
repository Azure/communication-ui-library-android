// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.notification

import com.azure.android.communication.ui.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.MediaCallDiagnostic
import com.azure.android.communication.ui.calling.models.UpperMessageBarNotificationModel
import com.azure.android.communication.ui.calling.presentation.fragment.calling.notification.UpperMessageBarNotificationViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class UpperMessageBarNotificationViewModelUnitTest : ACSBaseTestCoroutine() {

    @ExperimentalCoroutinesApi
    @Test
    fun upperMessageBarNotificationViewModel_init() {
        runScopedTest {
            // arrange
            val upperMessageBarNotificationModel = UpperMessageBarNotificationModel()
            upperMessageBarNotificationModel.notificationMessageId = 1
            upperMessageBarNotificationModel.notificationIconId = 2
            upperMessageBarNotificationModel.mediaCallDiagnostic = MediaCallDiagnostic.SPEAKER_MUTED

            val upperMessageBarNotificationViewModel = UpperMessageBarNotificationViewModel()
            upperMessageBarNotificationViewModel.init(upperMessageBarNotificationModel) {}

            // act
            val resultUpperMessageBarNotificationModelMessageFlow = mutableListOf<UpperMessageBarNotificationModel>()
            val flowJob = launch {
                upperMessageBarNotificationViewModel.getUpperMessageBarNotificationModelFlow().toList(resultUpperMessageBarNotificationModelMessageFlow)
            }

            // assert
            Assert.assertEquals(1, resultUpperMessageBarNotificationModelMessageFlow.last().notificationMessageId)
            Assert.assertEquals(2, resultUpperMessageBarNotificationModelMessageFlow.last().notificationIconId)
            Assert.assertEquals(MediaCallDiagnostic.SPEAKER_MUTED, resultUpperMessageBarNotificationModelMessageFlow.last().mediaCallDiagnostic)
            Assert.assertEquals(null, resultUpperMessageBarNotificationModelMessageFlow.last().notificationView)

            flowJob.cancel()
        }
    }
}
