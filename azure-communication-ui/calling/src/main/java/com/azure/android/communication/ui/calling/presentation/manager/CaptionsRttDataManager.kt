// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import android.graphics.Bitmap
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsData
import com.azure.android.communication.ui.calling.models.CaptionsResultType
import com.azure.android.communication.ui.calling.models.RttMessage
import com.azure.android.communication.ui.calling.presentation.fragment.calling.CallingFragment
import com.azure.android.communication.ui.calling.presentation.fragment.calling.captions.CaptionsRttRecord
import com.azure.android.communication.ui.calling.presentation.fragment.calling.captions.CaptionsRttType
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.CaptionsStatus
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.service.CallingService
import com.azure.android.communication.ui.calling.utilities.EventFlow
import com.azure.android.communication.ui.calling.utilities.MutableEventFlow
import com.azure.android.communication.ui.calling.utilities.launchAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.time.Instant
import java.util.Date
import kotlin.math.min

internal class CaptionsRttDataManager(
    private val callingService: CallingService,
    private val appStore: AppStore<ReduxState>,
    private val avatarViewManager: AvatarViewManager,
    private val localParticipantIdentifier: CommunicationIdentifier?,
    private val localParticipantDisplayName: String?,
) {
    private val mutex = Mutex()
    private var isCaptionsOn = false
    private val captionsAndRttMutableList = mutableListOf<CaptionsRttRecord>()
    private val mutableRecordRemovedAtPosition = MutableSharedFlow<Int>()
    private val mutableRecordUpdatedAtPosition = MutableSharedFlow<Int>()
    private val mutableRecordInsertedAtPosition = MutableSharedFlow<Int>()
    private var isRttInfoItemAdded = false
    private val captionsRttUpdatedMutableEventFlow = MutableEventFlow()

    val captionsAndRttData: List<CaptionsRttRecord> = captionsAndRttMutableList
    val recordUpdatedAtPosition: SharedFlow<Int> = mutableRecordUpdatedAtPosition
    val recordInsertedAtPosition: SharedFlow<Int> = mutableRecordInsertedAtPosition
    val recordRemovedAtPosition: SharedFlow<Int> = mutableRecordRemovedAtPosition

    val captionsRttUpdated: EventFlow = captionsRttUpdatedMutableEventFlow

    fun start(coroutineScope: CoroutineScope) {
        coroutineScope.launchAll(
            {
                startCaptionsConsumption()
            },
            {
                startRttConsumption()
            },
            {
                startStateConsumption()
            },
            {
                startCleanupProcessing(coroutineScope)
            }
        )
    }

    private suspend fun startCaptionsConsumption() {
        callingService.getCaptionsReceivedSharedFlow().collect { captionData ->
            mutex.withLock {
                if (shouldSkipCaption(captionData)) return@collect

                val (captionText, languageCode) = getCaptionTextAndLanguage(captionData)
                val (customizedDisplayName, avatar) =
                    if (localParticipantIdentifier?.rawId == captionData.speakerRawId)
                        getLocalParticipantCustomizations()
                    else
                        getParticipantCustomizations(captionData.speakerRawId)

                val record = CaptionsRttRecord(
                    avatarBitmap = avatar,
                    displayName = customizedDisplayName ?: captionData.speakerName,
                    displayText = captionText,
                    speakerRawId = captionData.speakerRawId,
                    languageCode = languageCode,
                    isFinal = captionData.resultType == CaptionsResultType.FINAL,
                    timestamp = captionData.timestamp,
                    lastUpdated = Date(),
                    type = CaptionsRttType.CAPTIONS,
                )

                removeOverflownCaptionsFromCache()
                handleCaptionData(record)
            }
        }
    }

    private suspend fun startRttConsumption() {
        callingService.getRttFlow().collect { rttRecord ->
            mutex.withLock {
                val displayName = getRttSenderDisplayName(rttRecord)
                val (customizedDisplayName, avatar) = if (rttRecord.isLocal)
                    getLocalParticipantCustomizations()
                else
                    getParticipantCustomizations(rttRecord.senderUserRawId)
                val captionsRecord = CaptionsRttRecord(
                    avatarBitmap = avatar,
                    displayName = customizedDisplayName ?: displayName,
                    displayText = rttRecord.message,
                    speakerRawId = rttRecord.senderUserRawId,
                    languageCode = null,
                    isFinal = rttRecord.isFinalized,
                    timestamp = rttRecord.localCreatedTime,
                    lastUpdated = Date(),
                    type = CaptionsRttType.RTT,
                    isLocal = rttRecord.isLocal,
                    rttSequenceId = rttRecord.sequenceId,
                )

                removeOverflownCaptionsFromCache()
                handleRttData(captionsRecord)
            }
        }
    }

    private suspend fun startStateConsumption() {
        appStore.getStateFlow().collect { state ->
            mutex.withLock {
                if (state.rttState.isRttActive) {
                    ensureRttMessageIsDisplayed()
                }
                if (state.captionsState.status == CaptionsStatus.STARTED && !isCaptionsOn) {
                    isCaptionsOn = true
                }
                if (state.captionsState.status == CaptionsStatus.STOPPED && isCaptionsOn) {
                    isCaptionsOn = false
                    removeCaptions()
                }
            }
        }
    }

    private suspend fun startCleanupProcessing(
        coroutineScope1: CoroutineScope
    ) {
        while (coroutineScope1.isActive) {
            mutex.withLock {
                cleanDeadRecords()
            }
            delay(timeMillis = 10000)
        }
    }

    private fun shouldSkipCaption(captionData: CallCompositeCaptionsData): Boolean {
        // if translation is enabled and translation language is not set, skip the caption
        val activeCaptionLanguage = appStore.getCurrentState().captionsState.captionLanguage
        return !activeCaptionLanguage.isNullOrEmpty() && captionData.captionLanguage.isNullOrEmpty()
    }

    private suspend fun removeOverflownCaptionsFromCache() {
        if (captionsAndRttMutableList.size >= CallingFragment.MAX_CAPTIONS_DATA_SIZE) {
            removeAtIndex(0)
        }
    }

    private fun getCaptionTextAndLanguage(captionData: CallCompositeCaptionsData): Pair<String, String?> {
        return if (!captionData.captionText.isNullOrEmpty()) {
            captionData.captionText to captionData.captionLanguage
        } else {
            captionData.spokenText to captionData.spokenLanguage
        }
    }

    private suspend fun handleRttData(newCaptionsRecord: CaptionsRttRecord) {
        ensureRttMessageIsDisplayed()
        val lastCaptionFromSameUser = captionsAndRttMutableList.lastOrNull {
            it.rttSequenceId == newCaptionsRecord.rttSequenceId
        }

        if (lastCaptionFromSameUser != null) {
            if (newCaptionsRecord.displayText.isEmpty()) {
                val indexToBeRemoved = captionsAndRttMutableList.indexOf(lastCaptionFromSameUser)
                removeAtIndex(indexToBeRemoved)
            } else {
                updateCaptionsRttRecord(lastCaptionFromSameUser, newCaptionsRecord)
            }
        } else {
            if (newCaptionsRecord.displayText.isNotEmpty()) {
                addNewCaption(newCaptionsRecord)
            }
        }
    }

    private suspend fun ensureRttMessageIsDisplayed() {
        if (!isRttInfoItemAdded) {
            val rttInfoItem = CaptionsRttRecord(
                avatarBitmap = null,
                displayName = "",
                displayText = "",
                speakerRawId = "",
                languageCode = null,
                isFinal = true,
                timestamp = Date(),
                lastUpdated = Date(),
                type = CaptionsRttType.RTT_INFO,
            )
            addNewCaption(rttInfoItem)
            isRttInfoItemAdded = true
        }
    }

    private suspend fun handleCaptionData(newCaptionsRecord: CaptionsRttRecord) {
        var lastCaptionFromSameUser = getLastCaptionFromUser(newCaptionsRecord.speakerRawId, CaptionsRttType.CAPTIONS)

        if (lastCaptionFromSameUser != null && shouldFinalizeLastCaption(lastCaptionFromSameUser, newCaptionsRecord)) {
            lastCaptionFromSameUser = finalizeLastCaption(lastCaptionFromSameUser)
        }

        if (lastCaptionFromSameUser?.isFinal == false) {
            updateCaptionsRttRecord(lastCaptionFromSameUser, newCaptionsRecord)
        } else {
            addNewCaption(newCaptionsRecord)
        }
    }

    private fun getLastCaptionFromUser(speakerRawId: String?, type: CaptionsRttType): CaptionsRttRecord? {
        return captionsAndRttMutableList.lastOrNull { it.type == type && it.speakerRawId == speakerRawId }
    }

    private fun shouldFinalizeLastCaption(lastCaption: CaptionsRttRecord, newCaptionsRecord: CaptionsRttRecord): Boolean {
        val duration = Duration.between(
            Instant.ofEpochMilli(lastCaption.timestamp.time),
            Instant.ofEpochMilli(newCaptionsRecord.timestamp.time)
        )
        return duration.toMillis() > CallingFragment.MAX_CAPTIONS_PARTIAL_DATA_TIME_LIMIT
    }

    private suspend fun addNewCaption(data: CaptionsRttRecord) {
        var index = 0
        if (data.type == CaptionsRttType.CAPTIONS) {
            index = captionsAndRttMutableList.indexOfLast {
                it.type == CaptionsRttType.CAPTIONS || it.isFinal
            } + 1
        } else {
            index = captionsAndRttMutableList.size

            if (captionsAndRttMutableList.lastOrNull()?.isLocal == true &&
                captionsAndRttMutableList.lastOrNull()?.isFinal == false
            ) {
                index -= 1
            }
        }

        insertCaption(index, data)
    }

    private suspend fun updateCaptionsRttRecord(lastCaptionFromSameUser: CaptionsRttRecord, captionsRecord: CaptionsRttRecord) {
        val lastCaptionIndex = captionsAndRttMutableList.indexOf(lastCaptionFromSameUser)

        if (captionsRecord.type == CaptionsRttType.RTT) {
            var moveToIndex = captionsAndRttMutableList.indexOfLast {
                it.type == CaptionsRttType.CAPTIONS || it.isFinal
            } + 1
            moveToIndex = min(moveToIndex, captionsAndRttMutableList.size)

            if (captionsRecord.isFinal && lastCaptionIndex != moveToIndex) {
                insertCaption(moveToIndex, captionsRecord)
                removeAtIndex(lastCaptionIndex)
            } else {
                updateAtIndex(lastCaptionIndex, captionsRecord)
            }
        } else {
            updateAtIndex(lastCaptionIndex, captionsRecord)
        }
    }

    private suspend fun finalizeLastCaption(captionsRecord: CaptionsRttRecord): CaptionsRttRecord {
        val captionIndex = captionsAndRttMutableList.indexOf(captionsRecord)
        val finalizedCaptionsRecord = captionsRecord.copy(isFinal = true)
        captionsAndRttMutableList[captionIndex] = finalizedCaptionsRecord
        mutableRecordUpdatedAtPosition.emit(captionIndex)
        return finalizedCaptionsRecord
    }

    private fun getRttSenderDisplayName(rttRecord: RttMessage): String? {
        return if (rttRecord.isLocal) {
            localParticipantDisplayName
        } else {
            rttRecord.senderName
        }
    }
    private fun getLocalParticipantCustomizations(): Pair<String?, Bitmap?> {
        val localParticipantViewData =
            avatarViewManager.callCompositeLocalOptions?.participantViewData
        if (localParticipantViewData != null) {
            return Pair(
                localParticipantViewData.displayName,
                localParticipantViewData.avatarBitmap
            )
        }
        return Pair(null, null)
    }
    private fun getParticipantCustomizations(participantRawId: String?): Pair<String?, Bitmap?> {
        participantRawId?.let {
            val remoteParticipantViewData = avatarViewManager.getRemoteParticipantViewData(participantRawId)
            if (remoteParticipantViewData != null) {
                return Pair(
                    remoteParticipantViewData.displayName,
                    remoteParticipantViewData.avatarBitmap
                )
            }
        }
        return Pair(null, null)
    }

    private suspend fun insertCaption(index: Int, data: CaptionsRttRecord) {
        captionsAndRttMutableList.add(index, data)
        mutableRecordInsertedAtPosition.emit(index)
    }

    private suspend fun updateAtIndex(index: Int, data: CaptionsRttRecord) {
        captionsAndRttMutableList[index] = data
        mutableRecordUpdatedAtPosition.emit(index)
    }

    private suspend fun removeAtIndex(index: Int) {
        captionsAndRttMutableList.removeAt(index)
        mutableRecordRemovedAtPosition.emit(index)
    }

    private fun removeCaptions() {
        captionsAndRttMutableList.removeAll { it.type == CaptionsRttType.CAPTIONS }
        captionsRttUpdatedMutableEventFlow.emit()
    }

    private suspend fun cleanDeadRecords() {
        for (i in captionsAndRttData.indices) {
            val item = captionsAndRttData[i]
            if (!item.isFinal && item.lastUpdated < Date.from(Instant.now().minusSeconds(10))) {
                updateAtIndex(i, item.copy(isFinal = true))
            }
        }
    }
}
