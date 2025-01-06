// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.ACSBaseTestCoroutine
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsData
import com.azure.android.communication.ui.calling.models.CaptionsResultType
import com.azure.android.communication.ui.calling.models.RttMessage
import com.azure.android.communication.ui.calling.presentation.fragment.calling.captions.CaptionsRttRecord
import com.azure.android.communication.ui.calling.presentation.fragment.calling.captions.CaptionsRttType
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.AppReduxState
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.service.CallingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
    @Mock
    private lateinit var avatarViewManager: AvatarViewManager

    private lateinit var captionsDataManager: CaptionsDataManager

    @Before
    fun setUp() {
        captionsDataManager = CaptionsDataManager(
            callingService = callingService,
            appStore = appStore,
            avatarViewManager = avatarViewManager,
            localParticipantIdentifier = null,
            localParticipantDisplayName = null,
        )
    }

    @Test
    @ExperimentalCoroutinesApi
    fun captionsDataManagerUnitTest_when_onCaptionsFlowUpdate_notifyCaptionText() {
        runScopedTest {
            // Arrange
            val appState = AppReduxState(
                displayName = "",
            )

            val captionsSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val rttSharedFlow = MutableSharedFlow<RttMessage>()
            val appStateFlow = MutableStateFlow<ReduxState>(appState)

            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(captionsSharedFlow)
            `when`(callingService.getRttFlow()).thenReturn(rttSharedFlow)
            `when`(appStore.getStateFlow()).thenReturn(appStateFlow)
            `when`(appStore.getCurrentState()).thenReturn(appState)
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

            val addedData = mutableListOf<Int>()

            val insertedJob = launch {
                captionsDataManager.recordInsertedAtPositionSharedFlow.toList(addedData)
            }

            // Act
            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))
            val flowJob = launch {
                captionsSharedFlow.emit(captionData)
            }
            flowJob.join()

            // Assert
            assertEquals(1, addedData.size)
            assertEquals(0, addedData[0])

            val newCaption = captionsDataManager.captionsAndRttData.last()
            assertNotNull(newCaption)
            assertEquals("Hello", newCaption.displayText)
            assertEquals("Speaker", newCaption.displayName)
            assertEquals("123", newCaption.speakerRawId)
            assertEquals(timestamp, newCaption.timestamp)
            assertEquals("en", newCaption.languageCode)
            assertTrue(newCaption.isFinal)
            assertNull(newCaption.isLocal)
            assertEquals(CaptionsRttType.CAPTIONS, newCaption.type)
            flowJob.cancel()
            insertedJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun captionsDataManagerUnitTest_when_onRttFlowUpdate_notifyCaptionText() {
        runScopedTest {
            // Arrange
            val appState = AppReduxState(
                displayName = "",
            )

            val captionsSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val rttSharedFlow = MutableSharedFlow<RttMessage>()
            val appStateFlow = MutableStateFlow<ReduxState>(appState)

            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(captionsSharedFlow)
            `when`(callingService.getRttFlow()).thenReturn(rttSharedFlow)
            `when`(appStore.getStateFlow()).thenReturn(appStateFlow)
            val timestamp = Date()
            val rttMessage = RttMessage(
                senderName = "Speaker",
                message = "Hello",
                senderUserRawId = "123",
                isFinalized = true,
                isLocal = false,
                localCreatedTime = timestamp,
            )

            val addedData = mutableListOf<Int>()

            val insertedJob = launch {
                captionsDataManager.recordInsertedAtPositionSharedFlow.toList(addedData)
            }

            // Act
            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))
            val flowJob = launch {
                rttSharedFlow.emit(rttMessage)
            }
            flowJob.join()

            // Assert
            assertEquals(2, addedData.size)
            assertEquals(0, addedData[0])
            assertEquals(1, addedData[1])

            val rttInfoMessage = captionsDataManager.captionsAndRttData[0]

            assertNotNull(rttInfoMessage)
            assertTrue(rttInfoMessage.isFinal)
            assertEquals(CaptionsRttType.RTT_INFO, rttInfoMessage.type)

            val newMessage = captionsDataManager.captionsAndRttData[1]

            assertNotNull(newMessage)
            assertEquals("Hello", newMessage.displayText)
            assertEquals("Speaker", newMessage.displayName)
            assertEquals("123", newMessage.speakerRawId)
            assertEquals(timestamp, newMessage.timestamp)
            assertTrue(newMessage.isFinal)
            assertEquals(false, newMessage.isLocal)
            assertEquals(CaptionsRttType.RTT, newMessage.type)

            flowJob.cancel()
            insertedJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun captionsDataManagerUnitTest_when_onCaptionsFlowUpdate_notifySpokenText() {
        runScopedTest {
            // Arrange
            val appState = AppReduxState(
                displayName = "",
            )

            val captionsSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val rttSharedFlow = MutableSharedFlow<RttMessage>()
            val appStateFlow = MutableStateFlow<ReduxState>(appState)

            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(captionsSharedFlow)
            `when`(callingService.getRttFlow()).thenReturn(rttSharedFlow)
            `when`(appStore.getStateFlow()).thenReturn(appStateFlow)
            `when`(appStore.getCurrentState()).thenReturn(appState)
            val timestamp = Date()
            val captionData = CallCompositeCaptionsData(
                speakerName = "Speaker",
                captionText = "Hello",
                speakerRawId = "123",
                captionLanguage = "en",
                spokenText = "aaa",
                spokenLanguage = "bb",
                resultType = CaptionsResultType.PARTIAL,
                timestamp = timestamp
            )

            val addedData = mutableListOf<Int>()
            val updatedData = mutableListOf<Int>()

            val insertedJob = launch {
                captionsDataManager.recordInsertedAtPositionSharedFlow.toList(addedData)
            }

            val updatedJob = launch {
                captionsDataManager.recordUpdatedAtPositionSharedFlow.toList(updatedData)
            }

            // Act
            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))
            val flowJob = launch {
                captionsSharedFlow.emit(captionData)
                val updatedCaptionData = CallCompositeCaptionsData(
                    speakerName = "Speaker",
                    captionText = "Hello World",
                    speakerRawId = "123",
                    captionLanguage = "en",
                    spokenText = "aaa",
                    spokenLanguage = "bb",
                    resultType = CaptionsResultType.FINAL,
                    timestamp = timestamp
                )
                captionsSharedFlow.emit(updatedCaptionData)
            }
            flowJob.join()

            // Assert
            assertEquals(1, addedData.size)
            assertEquals(0, addedData[0])

            assertEquals(1, updatedData.size)
            assertEquals(0, updatedData[0])

            val newCaption = captionsDataManager.captionsAndRttData.last()
            assertNotNull(newCaption)
            assertEquals("Hello World", newCaption.displayText)
            assertEquals("Speaker", newCaption.displayName)
            assertEquals("123", newCaption.speakerRawId)
            assertEquals(timestamp, newCaption.timestamp)
            assertEquals("en", newCaption.languageCode)
            assertTrue(newCaption.isFinal)
            assertNull(newCaption.isLocal)
            assertEquals(CaptionsRttType.CAPTIONS, newCaption.type)

            flowJob.cancel()
            insertedJob.cancel()
            updatedJob.cancel()
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun captionsDataManagerUnitTest_when_onRttFlowUpdate_notifySpokenText() {
        runScopedTest {
            // Arrange
            val appState = AppReduxState(
                displayName = "",
            )

            val captionsSharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
            val rttSharedFlow = MutableSharedFlow<RttMessage>()
            val appStateFlow = MutableStateFlow<ReduxState>(appState)

            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(captionsSharedFlow)
            `when`(callingService.getRttFlow()).thenReturn(rttSharedFlow)
            `when`(appStore.getStateFlow()).thenReturn(appStateFlow)
            val timestamp = Date()
            val rttMessage = RttMessage(
                senderName = "Speaker",
                message = "Hello",
                senderUserRawId = "123",
                isFinalized = false,
                isLocal = false,
                localCreatedTime = timestamp,
            )

            val addedData = mutableListOf<Int>()
            val updatedData = mutableListOf<Int>()

            val insertedJob = launch {
                captionsDataManager.recordInsertedAtPositionSharedFlow.toList(addedData)
            }

            val updatedJob = launch {
                captionsDataManager.recordUpdatedAtPositionSharedFlow.toList(updatedData)
            }

            // Act
            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))

            val flowJob = launch {
                rttSharedFlow.emit(rttMessage)
                val updatedRttData = RttMessage(
                    senderName = "Speaker",
                    message = "Hello World",
                    senderUserRawId = "123",
                    isFinalized = true,
                    isLocal = false,
                    localCreatedTime = timestamp,
                )
                rttSharedFlow.emit(updatedRttData)
            }
            flowJob.join()

            // Assert
            assertEquals(2, addedData.size)

            assertEquals(1, updatedData.size)
            assertEquals(1, updatedData[0])

            val newMessage = captionsDataManager.captionsAndRttData.last()
            assertNotNull(newMessage)
            assertEquals("Hello World", newMessage.displayText)
            assertEquals("Speaker", newMessage.displayName)
            assertEquals("123", newMessage.speakerRawId)
            assertEquals(timestamp, newMessage.timestamp)
            assertTrue(newMessage.isFinal)
            assertEquals(false, newMessage.isLocal)
            assertEquals(CaptionsRttType.RTT, newMessage.type)

            flowJob.cancel()
            insertedJob.cancel()
            updatedJob.cancel()
        }
    }

//    @Test
//    @ExperimentalCoroutinesApi
//    fun captionsDataManagerUnitTest_when_captionLanguageIsActive_doesNotNotifyIfCaptionTextIsBlank() {
//        runScopedTest {
//            // Arrange
//            val sharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
//            val currentState = AppReduxState(displayName = "")
//            currentState.captionsState = CaptionsState(
//                captionLanguage = "en",
//                spokenLanguage = "en",
//                isCaptionsUIEnabled = true,
//                isTranslationSupported = true,
//                status = CaptionsStatus.STARTED,
//            )
//            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(sharedFlow)
//            `when`(appStore.getCurrentState()).thenReturn(currentState)
//            val timestamp = Date()
//            val captionData = CallCompositeCaptionsData(
//                speakerName = "Speaker",
//                captionText = "",
//                speakerRawId = "123",
//                captionLanguage = "",
//                spokenText = "abc",
//                spokenLanguage = "def",
//                resultType = CaptionsResultType.PARTIAL,
//                timestamp = timestamp
//            )
//
//            // Act
//            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))
//            val flowJob = launch {
//                sharedFlow.emit(captionData)
//            }
//            flowJob.join()
//
//            // Assert
//            val newCaption = captionsDataManager.getOnNewCaptionsDataAddedStateFlow().value
//            assertNull(newCaption)
//            flowJob.cancel()
//        }
//    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun captionsDataManagerUnitTest_when_stateFlowUpdated_multipleUpdates() {
//        runScopedTest {
//            // Arrange
//            val sharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
//            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(sharedFlow)
//            `when`(appStore.getCurrentState()).thenReturn(
//                AppReduxState(
//                    displayName = "",
//                )
//            )
//
//            val timestamp = Date()
//            val captionDataAdd1 = createCaptionData("123", "abc", "def", CaptionsResultType.PARTIAL, timestamp)
//            val captionDataUpdate1 = createCaptionData("123", "abc234", "def", CaptionsResultType.FINAL, Date())
//            val captionDataAdd2 = createCaptionData("456", "xyz", "uvw", CaptionsResultType.PARTIAL, Date())
//            val captionDataUpdate2 = createCaptionData("456", "xyz890", "uvw", CaptionsResultType.PARTIAL, Date())
//
//            val addedData = mutableListOf<Int>()
//            val updatedData = mutableListOf<Int>()
//
//            // Act
//            val insertedJob = launch {
//                captionsDataManager.recordInsertedAtPositionSharedFlow.toList(addedData)
//            }
//
//            val updatedJob = launch {
//                captionsDataManager.recordUpdatedAtPositionSharedFlow.toList(updatedData)
//            }
//
//            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))
//
//            val jobList = mutableListOf<Job>()
//            val data = listOf(captionDataAdd1, captionDataUpdate1, captionDataAdd2, captionDataUpdate2)
//            data.forEach {
//                val job = launch {
//                    sharedFlow.emit(it)
//                }
//                jobList.add(job)
//                job.join()
//            }
//
//            // Assert
//            assertEquals(3, addedData.size)
//            assertEquals(3, updatedData.size)
//
//            assertNull(addedData[0])
//            assertNull(updatedData[0])
//
//            val addedData1 = captionsDataManager.captionsAndRttData[1]
//            val addedData2 = captionsDataManager.captionsAndRttData[2]
//
//            assertEquals("abc", addedData1.displayText)
//            assertEquals("abc234", addedData1.displayText)
//
//            assertEquals("xyz", addedData2.displayText)
//            assertEquals("xyz890", addedData2.displayText)
//
//            assertEquals(2, captionsDataManager.captionsAndRttData.size)
//
//            insertedJob.cancel()
//            updatedJob.cancel()
//            jobList.forEach {
//                it.cancel()
//            }
//        }
//    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun captionsDataManagerUnitTest_when_stateFlowUpdated_multipleUpdatesOnlyForLastSpeaker() {
//        runScopedTest {
//            // Arrange
//            val sharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
//            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(sharedFlow)
//            `when`(appStore.getCurrentState()).thenReturn(
//                AppReduxState(
//                    displayName = "",
//                )
//            )
//
//            val timestamp = Date()
//            val captionDataAdd1 = createCaptionData("123", "abc", "def", CaptionsResultType.PARTIAL, timestamp)
//            val captionDataUpdate1 = createCaptionData("123", "abc234", "def", CaptionsResultType.PARTIAL, Date())
//            val captionDataAdd2 = createCaptionData("456", "xyz", "uvw", CaptionsResultType.PARTIAL, Date())
//            val captionDataUpdate2 = createCaptionData("456", "xyz890", "uvw", CaptionsResultType.PARTIAL, Date())
//
//            val addedData = mutableListOf<CaptionsRecord?>()
//            val updatedData = mutableListOf<CaptionsRecord?>()
//
//            // Act
//            val addedDataJob = collectStateFlow(captionsDataManager.getOnNewCaptionsDataAddedStateFlow(), addedData)
//            val updatedDataJob = collectStateFlow(captionsDataManager.getOnLastCaptionsDataUpdatedStateFlow(), updatedData)
//
//            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))
//
//            val jobList = mutableListOf<Job>()
//            val data = listOf(captionDataAdd1, captionDataUpdate1, captionDataAdd2, captionDataUpdate2)
//            data.forEach {
//                val job = launch {
//                    sharedFlow.emit(it)
//                }
//                jobList.add(job)
//                job.join()
//            }
//
//            // Assert
//            assertEquals(2, addedData.size)
//            assertEquals(2, updatedData.size)
//
//            assertNull(addedData[0])
//            assertNull(updatedData[0])
//
//            assertEquals("abc", addedData[1]?.displayText)
//            assertEquals("abc234", updatedData[1]?.displayText)
//
//            assertEquals(1, captionsDataManager.captionsDataCache.size)
//
//            addedDataJob.cancel()
//            updatedDataJob.cancel()
//            jobList.forEach {
//                it.cancel()
//            }
//        }
//    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun captionsDataManagerUnitTest_when_stateFlowAddedUpdated_maxSize() {
//        runScopedTest {
//            // Arrange
//            val sharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
//            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(sharedFlow)
//            `when`(appStore.getCurrentState()).thenReturn(
//                AppReduxState(
//                    displayName = "",
//                )
//            )
//
//            val timestamp = Date()
//            val captionsDataList = mutableListOf<CallCompositeCaptionsData>()
//            for (i in 0 until 60) {
//                captionsDataList.add(createCaptionData(i.toString(), "abc", "def", CaptionsResultType.FINAL, timestamp))
//            }
//
//            val addedData = mutableListOf<CaptionsRecord?>()
//            val updatedData = mutableListOf<CaptionsRecord?>()
//
//            // Act
//            val addedDataJob = collectStateFlow(captionsDataManager.getOnNewCaptionsDataAddedStateFlow(), addedData)
//            val updatedDataJob = collectStateFlow(captionsDataManager.getOnLastCaptionsDataUpdatedStateFlow(), updatedData)
//
//            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))
//
//            val jobList = mutableListOf<Job>()
//            captionsDataList.forEach {
//                val job = launch {
//                    sharedFlow.emit(it)
//                }
//                jobList.add(job)
//                job.join()
//            }
//
//            // Assert
//            assertEquals(61, addedData.size)
//            assertEquals(1, updatedData.size)
//
//            assertNull(addedData[0])
//            assertNull(updatedData[0])
//            assertEquals(50, captionsDataManager.captionsDataCache.size)
//
//            addedDataJob.cancel()
//            updatedDataJob.cancel()
//            jobList.forEach {
//                it.cancel()
//            }
//        }
//    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun captionsDataManagerUnitTest_when_stateFlowUpdated_bothAddAndUpdate_ifDelayIsGreaterThanFewMs() {
//        runScopedTest {
//            // Arrange
//            val sharedFlow = MutableSharedFlow<CallCompositeCaptionsData>()
//            `when`(callingService.getCaptionsReceivedSharedFlow()).thenReturn(sharedFlow)
//            `when`(appStore.getCurrentState()).thenReturn(
//                AppReduxState(
//                    displayName = "",
//                )
//            )
//
//            val timestamp = Date()
//            val timestampUpdated = Date().apply {
//                time += 6000
//            }
//            val captionDataAdd1 = createCaptionData("123", "abc", "def", CaptionsResultType.PARTIAL, timestamp)
//            val captionDataUpdate1 = createCaptionData("234", "abc234", "def", CaptionsResultType.PARTIAL, timestampUpdated)
//
//            val addedData = mutableListOf<CaptionsRecord?>()
//            val updatedData = mutableListOf<CaptionsRecord?>()
//
//            // Act
//            val addedDataJob = collectStateFlow(captionsDataManager.getOnNewCaptionsDataAddedStateFlow(), addedData)
//            val updatedDataJob = collectStateFlow(captionsDataManager.getOnLastCaptionsDataUpdatedStateFlow(), updatedData)
//
//            captionsDataManager.start(TestScope(UnconfinedTestDispatcher()))
//
//            val jobList = mutableListOf<Job>()
//            val data = listOf(captionDataAdd1, captionDataUpdate1)
//            data.forEach {
//                val job = launch {
//                    sharedFlow.emit(it)
//                }
//                jobList.add(job)
//                job.join()
//            }
//
//            // Assert
//            assertEquals(3, addedData.size)
//            assertEquals(2, updatedData.size)
//
//            assertNull(addedData[0])
//            assertNull(updatedData[0])
//
//            assertEquals("abc", addedData[1]?.displayText)
//            assertEquals("abc", updatedData[1]?.displayText)
//            assertEquals("abc234", addedData[2]?.displayText)
//
//            assertEquals(2, captionsDataManager.captionsDataCache.size)
//
//            addedDataJob.cancel()
//            updatedDataJob.cancel()
//            jobList.forEach {
//                it.cancel()
//            }
//        }
//    }

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
        stateFlow: StateFlow<CaptionsRttRecord?>,
        collector: MutableList<CaptionsRttRecord?>
    ) = launch {
        stateFlow.toList(collector)
    }
}
