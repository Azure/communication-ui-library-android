// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsData
import com.azure.android.communication.ui.calling.models.CaptionsResultType
import com.azure.android.communication.ui.calling.presentation.fragment.calling.CallingFragment
import com.azure.android.communication.ui.calling.presentation.fragment.calling.captions.CaptionsRecord
import com.azure.android.communication.ui.calling.presentation.fragment.calling.captions.CaptionsRttType
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.service.CallingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.time.Instant

internal class CaptionsDataManager(
    private val callingService: CallingService,
    private val appStore: AppStore<ReduxState>
) {
    private val mutex = Mutex()
    private val captionsNewDataStateFlow = MutableStateFlow<CaptionsRecord?>(null)
    private val captionsLastDataUpdatedStateFlow = MutableStateFlow<CaptionsRecord?>(null)

    fun getOnNewCaptionsDataAddedStateFlow() = captionsNewDataStateFlow

    fun getOnLastCaptionsDataUpdatedStateFlow() = captionsLastDataUpdatedStateFlow

    // cache to get last captions on screen rotation
    val captionsDataCache = mutableListOf<CaptionsRecord>()

    fun resetFlows() {
        captionsNewDataStateFlow.value = null
        captionsLastDataUpdatedStateFlow.value = null
    }

    fun start(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            callingService.getCaptionsReceivedSharedFlow().collect { captionData ->
                mutex.withLock {
                    if (shouldSkipCaption(captionData)) return@collect

                    removeOverflownCaptionsFromCache()

                    val (captionText, languageCode) = getCaptionTextAndLanguage(captionData)

                    val captionsRecord = CaptionsRecord(
                        captionData.speakerName,
                        captionText,
                        captionData.speakerRawId,
                        languageCode,
                        captionData.resultType == CaptionsResultType.FINAL,
                        captionData.timestamp,
                        CaptionsRttType.CAPTIONS
                    )

                    handleCaptionData(captionsRecord)
                }
            }
        }

        coroutineScope.launch {
            callingService.getRttStateFlow().collect { rttRecord ->
                mutex.withLock {
                    removeOverflownCaptionsFromCache()
                    val captionsRecord = CaptionsRecord(
                        rttRecord.senderName,
                        rttRecord.message,
                        rttRecord.senderUserRawId,
                        null,
                        rttRecord.isFinalized,
                        rttRecord.localCreatedTime,
                        CaptionsRttType.RTT
                    )

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

    private fun removeOverflownCaptionsFromCache() {
        if (captionsDataCache.size >= CallingFragment.MAX_CAPTIONS_DATA_SIZE) {
            captionsDataCache.removeAt(0)
        }
    }

    private fun getCaptionTextAndLanguage(captionData: CallCompositeCaptionsData): Pair<String, String?> {
        return if (!captionData.captionText.isNullOrEmpty()) {
            captionData.captionText to captionData.captionLanguage
        } else {
            captionData.spokenText to captionData.spokenLanguage
        }
    }

    private fun handleRttData(newCaptionsRecord: CaptionsRecord) {
        val lastCaptionFromSameUser: CaptionsRecord? = getLastCaptionFromUser(newCaptionsRecord.speakerRawId, CaptionsRttType.RTT)

        if (lastCaptionFromSameUser?.isFinal == false) {
            updateLastCaption(lastCaptionFromSameUser, newCaptionsRecord)
        } else {
            addNewCaption(newCaptionsRecord)
        }
    }

    private fun handleCaptionData(newCaptionsRecord: CaptionsRecord) {
        var lastCaptionFromSameUser: CaptionsRecord? = getLastCaptionFromUser(newCaptionsRecord.speakerRawId, CaptionsRttType.CAPTIONS)

        if (lastCaptionFromSameUser != null && shouldFinalizeLastCaption(lastCaptionFromSameUser, newCaptionsRecord)) {
            lastCaptionFromSameUser = finalizeLastCaption(lastCaptionFromSameUser)
        }

        if (lastCaptionFromSameUser?.isFinal == false) {
            updateLastCaption(lastCaptionFromSameUser, newCaptionsRecord)
        } else {
            addNewCaption(newCaptionsRecord)
        }
    }

    private fun getLastCaptionFromUser(speakerRawId: String, type: CaptionsRttType): CaptionsRecord? {
        return captionsDataCache.lastOrNull { it.type == type && it.speakerRawId == speakerRawId }
    }

    private fun shouldFinalizeLastCaption(lastCaption: CaptionsRecord, newCaptionsRecord: CaptionsRecord): Boolean {
        val duration = Duration.between(Instant.ofEpochMilli(lastCaption.timestamp.time), Instant.ofEpochMilli(newCaptionsRecord.timestamp.time))
        return duration.toMillis() > CallingFragment.MAX_CAPTIONS_PARTIAL_DATA_TIME_LIMIT
    }

    private fun addNewCaption(data: CaptionsRecord) {
        captionsNewDataStateFlow.value = data
        captionsDataCache.add(data)
    }

    private fun updateLastCaption(lastCaptionFromSameUser: CaptionsRecord, captionsRecord: CaptionsRecord) {
        val lastCaptionIndex = captionsDataCache.indexOf(lastCaptionFromSameUser)
        captionsDataCache[lastCaptionIndex] = captionsRecord
        captionsLastDataUpdatedStateFlow.value = captionsRecord
    }

    private fun finalizeLastCaption(captionsRecord: CaptionsRecord): CaptionsRecord {
        val captionIndex = captionsDataCache.indexOf(captionsRecord)
        val finalizedCaptionsRecord = captionsRecord.copy(isFinal = true)
        captionsDataCache[captionIndex] = finalizedCaptionsRecord
        captionsLastDataUpdatedStateFlow.value = captionsRecord
        return finalizedCaptionsRecord
    }
}
