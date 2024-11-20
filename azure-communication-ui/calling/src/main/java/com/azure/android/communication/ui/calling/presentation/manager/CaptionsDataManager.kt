// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import android.graphics.Bitmap
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsData
import com.azure.android.communication.ui.calling.models.CaptionsResultType
import com.azure.android.communication.ui.calling.presentation.fragment.calling.CallingFragment
import com.azure.android.communication.ui.calling.presentation.fragment.calling.captions.CaptionsRttRecord
import com.azure.android.communication.ui.calling.presentation.fragment.calling.captions.CaptionsRttType
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.service.CallingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.time.Instant

internal class CaptionsDataManager(
    private val callingService: CallingService,
    private val appStore: AppStore<ReduxState>,
    private val avatarViewManager: AvatarViewManager,
    private val localParticipantIdentifier: CommunicationIdentifier?,
) {
    private val mutex = Mutex()
    private val captionsAndRttMutableList = mutableListOf<CaptionsRttRecord>()
    private val recordUpdatedAtPositionMutableSharedFlow = MutableSharedFlow<Int>()
    private val recordInsertedAtPositionMutableSharedFlow = MutableSharedFlow<Int>()
    private val recordRemovedAtPositionMutableSharedFlow = MutableSharedFlow<Int>()

    val captionsAndRttData: List<CaptionsRttRecord> = captionsAndRttMutableList
    val recordUpdatedAtPositionSharedFlow: SharedFlow<Int> = recordUpdatedAtPositionMutableSharedFlow
    val recordInsertedAtPositionSharedFlow: SharedFlow<Int> = recordInsertedAtPositionMutableSharedFlow
    val recordRemovedAtPositionSharedFlow: SharedFlow<Int> = recordRemovedAtPositionMutableSharedFlow

    fun start(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            callingService.getCaptionsReceivedSharedFlow().collect { captionData ->
                mutex.withLock {
                    if (shouldSkipCaption(captionData)) return@collect

                    val (captionText, languageCode) = getCaptionTextAndLanguage(captionData)
                    val (customizedDisplayName, avatar) = getParticipantCustomizationsBitmap(captionData.speakerRawId)
                    val record = CaptionsRttRecord(
                        avatarBitmap = avatar,
                        displayName = customizedDisplayName ?: captionData.speakerName,
                        displayText = captionText,
                        speakerRawId = captionData.speakerRawId,
                        languageCode = languageCode,
                        isFinal = captionData.resultType == CaptionsResultType.FINAL,
                        timestamp = captionData.timestamp,
                        type = CaptionsRttType.CAPTIONS,
                    )

                    removeOverflownCaptionsFromCache()
                    handleCaptionData(record)
                }
            }
        }

        coroutineScope.launch {
            callingService.getRttStateFlow().collect { rttRecord ->
                mutex.withLock {
                    val (customizedDisplayName, avatar) = getParticipantCustomizationsBitmap(rttRecord.senderUserRawId)
                    val captionsRecord = CaptionsRttRecord(
                        avatarBitmap = avatar,
                        displayName = customizedDisplayName ?: rttRecord.senderName,
                        displayText = rttRecord.message,
                        speakerRawId = rttRecord.senderUserRawId,
                        languageCode = null,
                        isFinal = rttRecord.isFinalized,
                        timestamp = rttRecord.localCreatedTime,
                        type = CaptionsRttType.RTT,
                        isLocal = rttRecord.isLocal,
                    )

                    removeOverflownCaptionsFromCache()
                    handleRttData(captionsRecord)
                }
            }
        }
    }

    private fun shouldSkipCaption(captionData: CallCompositeCaptionsData): Boolean {
        // if translation is enabled and translation language is not set, skip the caption
        val activeCaptionLanguage = appStore.getCurrentState().captionsState.captionLanguage
        return !activeCaptionLanguage.isNullOrEmpty() && captionData.captionLanguage.isNullOrEmpty()
    }

    private suspend fun removeOverflownCaptionsFromCache() {
        if (captionsAndRttMutableList.size >= CallingFragment.MAX_CAPTIONS_DATA_SIZE) {
            captionsAndRttMutableList.removeAt(0)
            recordRemovedAtPositionMutableSharedFlow.emit(0)
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
        val lastCaptionFromSameUser = getLastCaptionFromUser(newCaptionsRecord.speakerRawId, CaptionsRttType.RTT)

        if (lastCaptionFromSameUser?.isFinal == false) {
            updateLastCaption(lastCaptionFromSameUser, newCaptionsRecord)
        } else {
            addNewCaption(newCaptionsRecord)
        }
    }

    private suspend fun handleCaptionData(newCaptionsRecord: CaptionsRttRecord) {
        var lastCaptionFromSameUser = getLastCaptionFromUser(newCaptionsRecord.speakerRawId, CaptionsRttType.CAPTIONS)

        if (lastCaptionFromSameUser != null && shouldFinalizeLastCaption(lastCaptionFromSameUser, newCaptionsRecord)) {
            lastCaptionFromSameUser = finalizeLastCaption(lastCaptionFromSameUser)
        }

        if (lastCaptionFromSameUser?.isFinal == false) {
            updateLastCaption(lastCaptionFromSameUser, newCaptionsRecord)
        } else {
            addNewCaption(newCaptionsRecord)
        }
    }

    private fun getLastCaptionFromUser(speakerRawId: String, type: CaptionsRttType): CaptionsRttRecord? {
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
        captionsAndRttMutableList.add(data)
        recordInsertedAtPositionMutableSharedFlow.emit(captionsAndRttMutableList.size - 1)
    }

    private suspend fun updateLastCaption(lastCaptionFromSameUser: CaptionsRttRecord, captionsRecord: CaptionsRttRecord) {
        val lastCaptionIndex = captionsAndRttMutableList.indexOf(lastCaptionFromSameUser)
        captionsAndRttMutableList[lastCaptionIndex] = captionsRecord
        recordUpdatedAtPositionMutableSharedFlow.emit(lastCaptionIndex)
    }

    private suspend fun finalizeLastCaption(captionsRecord: CaptionsRttRecord): CaptionsRttRecord {
        val captionIndex = captionsAndRttMutableList.indexOf(captionsRecord)
        val finalizedCaptionsRecord = captionsRecord.copy(isFinal = true)
        captionsAndRttMutableList[captionIndex] = finalizedCaptionsRecord
        recordUpdatedAtPositionMutableSharedFlow.emit(captionIndex)
        return finalizedCaptionsRecord
    }

    private fun getParticipantCustomizationsBitmap(speakerRawId: String): Pair<String?, Bitmap?> {
        val remoteParticipantViewData = avatarViewManager.getRemoteParticipantViewData(speakerRawId)
        if (remoteParticipantViewData!= null) {
            return Pair(remoteParticipantViewData.displayName, remoteParticipantViewData.avatarBitmap)
        }

        val localParticipantViewData = avatarViewManager.callCompositeLocalOptions?.participantViewData
        if (localParticipantViewData != null && localParticipantIdentifier?.rawId == speakerRawId) {
            return Pair(localParticipantViewData.displayName, localParticipantViewData.avatarBitmap)
        }
        return Pair(null, null)
    }
}
