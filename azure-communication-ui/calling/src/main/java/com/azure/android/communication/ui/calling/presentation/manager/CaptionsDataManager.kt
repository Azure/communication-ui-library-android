// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsData
import com.azure.android.communication.ui.calling.models.CaptionsResultType
import com.azure.android.communication.ui.calling.presentation.fragment.calling.CallingFragment
import com.azure.android.communication.ui.calling.presentation.fragment.calling.captions.CaptionsDataViewModel
import com.azure.android.communication.ui.calling.redux.AppStore
import com.azure.android.communication.ui.calling.redux.state.ReduxState
import com.azure.android.communication.ui.calling.service.CallingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class CaptionsDataManager(
    private val callingService: CallingService,
    private val appStore: AppStore<ReduxState>
) {
    private val captionsNewDataStateFlow = MutableStateFlow<CaptionsDataViewModel?>(null)
    fun getOnNewCaptionsDataAddedStateFlow() = captionsNewDataStateFlow

    private val captionsLastDataUpdatedStateFlow = MutableStateFlow<CaptionsDataViewModel?>(null)
    fun getOnLastCaptionsDataUpdatedStateFlow() = captionsLastDataUpdatedStateFlow

    // cache to get last captions on screen rotation
    val captionsDataCache = mutableListOf<CaptionsDataViewModel>()

    fun start(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            callingService.getCaptionsReceivedSharedFlow().collect { captionData ->
                if (shouldSkipCaption(captionData)) return@collect

                updateCaptionsDataCache()

                val (captionText, languageCode) = extractCaptionTextAndLanguage(captionData)

                val data = CaptionsDataViewModel(
                    captionData.speakerName,
                    captionText,
                    captionData.speakerRawId,
                    languageCode,
                    captionData.resultType == CaptionsResultType.FINAL
                )

                handleCaptionData(captionData, data)
            }
        }
    }

    private fun shouldSkipCaption(captionData: CallCompositeCaptionsData): Boolean {
        val activeCaptionLanguage = appStore.getCurrentState().captionsState.activeCaptionLanguage
        return activeCaptionLanguage.isNotEmpty() && captionData.captionLanguage.isNullOrEmpty()
    }

    private fun updateCaptionsDataCache() {
        if (captionsDataCache.size >= CallingFragment.MAX_CAPTIONS_DATA_SIZE) {
            captionsDataCache.removeAt(0)
        }
    }

    private fun extractCaptionTextAndLanguage(captionData: CallCompositeCaptionsData): Pair<String, String> {
        return if (!captionData.captionText.isNullOrEmpty()) {
            captionData.captionText to (captionData.captionLanguage ?: "")
        } else {
            captionData.spokenText to captionData.spokenLanguage
        }
    }

    private fun handleCaptionData(captionData: CallCompositeCaptionsData, data: CaptionsDataViewModel) {
        val lastData = captionsDataCache.findLast {
            it.speakerRawIdentifierId == captionData.speakerRawId &&
                !it.isFinal
        }

        if (lastData != null) {
            captionsLastDataUpdatedStateFlow.value = data
            captionsDataCache[captionsDataCache.indexOf(lastData)] = data
        } else {
            captionsNewDataStateFlow.value = data
            captionsDataCache.add(data)
        }
    }
}
