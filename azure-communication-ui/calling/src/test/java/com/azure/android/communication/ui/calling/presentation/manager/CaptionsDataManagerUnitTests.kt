// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsData
import com.azure.android.communication.ui.calling.models.CaptionsResultType
import com.azure.android.communication.ui.calling.presentation.fragment.calling.captions.CaptionsRecord

import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.CaptionsState
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.service.CallingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
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
                captionText = null,
                speakerRawId = "123",
                captionLanguage = null,
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

    @OptIn(InternalCoroutinesApi::class)
    @Test
    @ExperimentalCoroutinesApi
    fun captionsDataManagerUnitTest_when_stateFlowUpdated_addAndUpdateNotified() {
        runScopedTest {
            // Arrange
            val sharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(sharedFlow)
            `when`(appStore.getCurrentState()).thenReturn(AppReduxState(displayName = ""))
            val timestamp = Date()
            val captionDataAdd = CallCompositeCaptionsData(
                speakerName = "Speaker",
                captionText = null,
                speakerRawId = "123",
                captionLanguage = "",
                spokenText = "abc",
                spokenLanguage = "def",
                resultType = CaptionsResultType.PARTIAL,
                timestamp = timestamp
            )
            val captionDataUpdate = CallCompositeCaptionsData(
                speakerName = "Speaker",
                captionText = null,
                speakerRawId = "123",
                captionLanguage = "",
                spokenText = "abc234",
                spokenLanguage = "def",
                resultType = CaptionsResultType.PARTIAL,
                timestamp = Date()
            )
            val addedData = mutableListOf<CaptionsRecord?>()
            val updatedData = mutableListOf<CaptionsRecord?>()

            // Act
            val addedDataJob = launch {
                captionsDataManager.getOnNewCaptionsDataAddedStateFlow().toList(addedData)
            }
            val updatedDataJob = launch {
                captionsDataManager.getOnLastCaptionsDataUpdatedStateFlow().toList(updatedData)
            }
            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))
            val flowJob = launch {
                sharedFlow.emit(captionDataAdd)
                sharedFlow.emit(captionDataUpdate)
            }
            flowJob.join()

            // Assert
            assertEquals(2, addedData.size)
            assertEquals(2, updatedData.size)
            assertNull(addedData[0])
            assertNull(updatedData[0])
            assertEquals("abc", addedData[1]?.displayText)
            assertEquals("abc234", updatedData[1]?.displayText)

            flowJob.cancel()
            addedDataJob.cancel()
            updatedDataJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun captionsDataManagerUnitTest_when_captionLanguageIsActive_doesNotNotifyIfCaptionTextIsBlank() {
        runScopedTest {
            // Arrange
            val sharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val currentState = AppReduxState(displayName = "")
            currentState.captionsState = CaptionsState(
                captionLanguage = "en",
                spokenLanguage = "en",
                isCaptionsUIEnabled = true,
                isTranslationSupported = true,
                status = CaptionsStatus.STARTED,
                showCaptionsToggleUI = true
            )
            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(sharedFlow)
            `when`(appStore.getCurrentState()).thenReturn(currentState)
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
            assertNull(newCaption)
            flowJob.cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsDataManagerUnitTest_when_stateFlowUpdated_multipleUpdates() {
        runScopedTest {
            // Arrange
            val sharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(sharedFlow)
            `when`(appStore.getCurrentState()).thenReturn(AppReduxState(displayName = ""))

            val timestamp = Date()
            val captionDataAdd1 = createCaptionData("123", "abc", "def", CaptionsResultType.PARTIAL, timestamp)
            val captionDataUpdate1 = createCaptionData("123", "abc234", "def", CaptionsResultType.FINAL, Date())
            val captionDataAdd2 = createCaptionData("456", "xyz", "uvw", CaptionsResultType.PARTIAL, Date())
            val captionDataUpdate2 = createCaptionData("456", "xyz890", "uvw", CaptionsResultType.PARTIAL, Date())

            val addedData = mutableListOf<CaptionsRecord?>()
            val updatedData = mutableListOf<CaptionsRecord?>()

            // Act
            val addedDataJob = collectStateFlow(captionsDataManager.getOnNewCaptionsDataAddedStateFlow(), addedData)
            val updatedDataJob = collectStateFlow(captionsDataManager.getOnLastCaptionsDataUpdatedStateFlow(), updatedData)

            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))

            val jobList = mutableListOf<Job>()
            val data = listOf(captionDataAdd1, captionDataUpdate1, captionDataAdd2, captionDataUpdate2)
            data.forEach {
                val job = launch {
                    sharedFlow.emit(it)
                }
                jobList.add(job)
                job.join()
            }

            // Assert
            assertEquals(3, addedData.size)
            assertEquals(3, updatedData.size)

            assertNull(addedData[0])
            assertNull(updatedData[0])

            assertEquals("abc", addedData[1]?.displayText)
            assertEquals("abc234", updatedData[1]?.displayText)

            assertEquals("xyz", addedData[2]?.displayText)
            assertEquals("xyz890", updatedData[2]?.displayText)

            assertEquals(2, captionsDataManager.captionsDataCache.size)

            addedDataJob.cancel()
            updatedDataJob.cancel()
            jobList.forEach {
                it.cancel()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsDataManagerUnitTest_when_stateFlowUpdated_multipleUpdatesOnlyForLastSpeaker() {
        runScopedTest {
            // Arrange
            val sharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(sharedFlow)
            `when`(appStore.getCurrentState()).thenReturn(AppReduxState(displayName = ""))

            val timestamp = Date()
            val captionDataAdd1 = createCaptionData("123", "abc", "def", CaptionsResultType.PARTIAL, timestamp)
            val captionDataUpdate1 = createCaptionData("123", "abc234", "def", CaptionsResultType.PARTIAL, Date())
            val captionDataAdd2 = createCaptionData("456", "xyz", "uvw", CaptionsResultType.PARTIAL, Date())
            val captionDataUpdate2 = createCaptionData("456", "xyz890", "uvw", CaptionsResultType.PARTIAL, Date())

            val addedData = mutableListOf<CaptionsRecord?>()
            val updatedData = mutableListOf<CaptionsRecord?>()

            // Act
            val addedDataJob = collectStateFlow(captionsDataManager.getOnNewCaptionsDataAddedStateFlow(), addedData)
            val updatedDataJob = collectStateFlow(captionsDataManager.getOnLastCaptionsDataUpdatedStateFlow(), updatedData)

            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))

            val jobList = mutableListOf<Job>()
            val data = listOf(captionDataAdd1, captionDataUpdate1, captionDataAdd2, captionDataUpdate2)
            data.forEach {
                val job = launch {
                    sharedFlow.emit(it)
                }
                jobList.add(job)
                job.join()
            }

            // Assert
            assertEquals(2, addedData.size)
            assertEquals(2, updatedData.size)

            assertNull(addedData[0])
            assertNull(updatedData[0])

            assertEquals("abc", addedData[1]?.displayText)
            assertEquals("abc234", updatedData[1]?.displayText)

            assertEquals(1, captionsDataManager.captionsDataCache.size)

            addedDataJob.cancel()
            updatedDataJob.cancel()
            jobList.forEach {
                it.cancel()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsDataManagerUnitTest_when_stateFlowAddedUpdated_maxSize() {
        runScopedTest {
            // Arrange
            val sharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(sharedFlow)
            `when`(appStore.getCurrentState()).thenReturn(AppReduxState(displayName = ""))

            val timestamp = Date()
            val captionsDataList = mutableListOf<CallCompositeCaptionsData>()
            for (i in 0 until 60) {
                captionsDataList.add(createCaptionData(i.toString(), "abc", "def", CaptionsResultType.FINAL, timestamp))
            }

            val addedData = mutableListOf<CaptionsRecord?>()
            val updatedData = mutableListOf<CaptionsRecord?>()

            // Act
            val addedDataJob = collectStateFlow(captionsDataManager.getOnNewCaptionsDataAddedStateFlow(), addedData)
            val updatedDataJob = collectStateFlow(captionsDataManager.getOnLastCaptionsDataUpdatedStateFlow(), updatedData)

            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))

            val jobList = mutableListOf<Job>()
            captionsDataList.forEach {
                val job = launch {
                    sharedFlow.emit(it)
                }
                jobList.add(job)
                job.join()
            }

            // Assert
            assertEquals(61, addedData.size)
            assertEquals(1, updatedData.size)

            assertNull(addedData[0])
            assertNull(updatedData[0])
            assertEquals(50, captionsDataManager.captionsDataCache.size)

            addedDataJob.cancel()
            updatedDataJob.cancel()
            jobList.forEach {
                it.cancel()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun captionsDataManagerUnitTest_when_stateFlowUpdated_bothAddAndUpdate_ifDelayIsGreaterThanFewMs() {
        runScopedTest {
            // Arrange
            val sharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(sharedFlow)
            `when`(appStore.getCurrentState()).thenReturn(AppReduxState(displayName = ""))

            val timestamp = Date()
            val timestampUpdated = Date().apply {
                time += 6000
            }
            val captionDataAdd1 = createCaptionData("123", "abc", "def", CaptionsResultType.PARTIAL, timestamp)
            val captionDataUpdate1 = createCaptionData("234", "abc234", "def", CaptionsResultType.PARTIAL, timestampUpdated)

            val addedData = mutableListOf<CaptionsRecord?>()
            val updatedData = mutableListOf<CaptionsRecord?>()

            // Act
            val addedDataJob = collectStateFlow(captionsDataManager.getOnNewCaptionsDataAddedStateFlow(), addedData)
            val updatedDataJob = collectStateFlow(captionsDataManager.getOnLastCaptionsDataUpdatedStateFlow(), updatedData)

            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))

            val jobList = mutableListOf<Job>()
            val data = listOf(captionDataAdd1, captionDataUpdate1)
            data.forEach {
                val job = launch {
                    sharedFlow.emit(it)
                }
                jobList.add(job)
                job.join()
            }

            // Assert
            assertEquals(3, addedData.size)
            assertEquals(2, updatedData.size)

            assertNull(addedData[0])
            assertNull(updatedData[0])

            assertEquals("abc", addedData[1]?.displayText)
            assertEquals("abc", updatedData[1]?.displayText)
            assertEquals("abc234", addedData[2]?.displayText)

            assertEquals(2, captionsDataManager.captionsDataCache.size)

            addedDataJob.cancel()
            updatedDataJob.cancel()
            jobList.forEach {
                it.cancel()
            }
        }
    }

    private fun createCaptionData(
        speakerRawId: String,
        spokenText: String,
        spokenLanguage: String,
        resultType: CaptionsResultType,
        timestamp: Date
    ): CallCompositeCaptionsData {
        return CallCompositeCaptionsData(
            speakerName = "Speaker",
            captionText = "",
            speakerRawId = speakerRawId,
            captionLanguage = "",
            spokenText = spokenText,
            spokenLanguage = spokenLanguage,
            resultType = resultType,
            timestamp = timestamp
        )
    }

    private fun CoroutineScope.collectStateFlow(
        stateFlow: StateFlow<CaptionsRecord?>,
        collector: MutableList<CaptionsRecord?>
    ) = launch {
        stateFlow.toList(collector)
    }
}
