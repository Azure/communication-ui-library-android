// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsData
import com.azure.android.communication.ui.calling.models.CaptionsResultType

import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.service.CallingService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.times
import java.util.Date

@RunWith(MockitoJUnitRunner::class)
internal class CaptionsDataManagerUnitTests : ACSBaseTestCoroutine() {
    @Mock
    private lateinit var callingService: CallingService
    @Mock
    private lateinit var appStore: AppStore<ReduxState>

    private lateinit var captionsDataManager: CaptionsDataManager

    @Before
    fun setUp() {
        captionsDataManager = CaptionsDataManager(callingService, appStore)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun captionsDataManagerUnitTest_when_onCaptionsFlowUpdate_notifyCaptionText() {
        runScopedTest {
            // Arrange
            val sharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(sharedFlow)
            `when`(appStore.getCurrentState()).thenReturn(AppReduxState(displayName = ""))
            val timestamp = Date()
            val captionData = CallCompositeCaptionsData(
                speakerName = "Speaker",
                captionText = "Hello",
                speakerRawId = "123",
                captionLanguage = "en",
                spokenText = "aaa",
                spokenLanguage = "bb",
                resultType = CaptionsResultType.FINAL,
                timestamp = timestamp
            )

            // Act
            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))
            val flowJob = launch {
                sharedFlow.emit(captionData)
            }
            flowJob.join()

            // Assert
            val newCaption = captionsDataManager.getOnNewCaptionsDataAddedStateFlow().value
            assertNotNull(newCaption)
            assertNotNull(newCaption)
            assertEquals("Hello", newCaption?.displayText)
            assertEquals("Speaker", newCaption?.displayName)
            assertEquals("123", newCaption?.speakerRawId)
            assertEquals(timestamp, newCaption?.timestamp)
            assertEquals("en", newCaption?.languageCode)
            assertEquals(true, newCaption?.isFinal)
            flowJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun captionsDataManagerUnitTest_when_onCaptionsFlowUpdate_notifySpokenText() {
        runScopedTest {
            // Arrange
            val sharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(sharedFlow)
            `when`(appStore.getCurrentState()).thenReturn(AppReduxState(displayName = ""))
            val timestamp = Date()
            val captionData = CallCompositeCaptionsData(
                speakerName = "Speaker",
                captionText = "",
                speakerRawId = "123",
                captionLanguage = "",
                spokenText = "abc",
                spokenLanguage = "def",
                resultType = CaptionsResultType.PARTIAL,
                timestamp = timestamp
            )

            // Act
            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))
            val flowJob = launch {
                sharedFlow.emit(captionData)
            }
            flowJob.join()

            // Assert
            val newCaption = captionsDataManager.getOnNewCaptionsDataAddedStateFlow().value
            assertNotNull(newCaption)
            assertEquals("abc", newCaption?.displayText)
            assertEquals("Speaker", newCaption?.displayName)
            assertEquals("123", newCaption?.speakerRawId)
            assertEquals(timestamp, newCaption?.timestamp)
            assertEquals("def", newCaption?.languageCode)
            assertEquals(false, newCaption?.isFinal)
            flowJob.cancel()
        }
    }
}
