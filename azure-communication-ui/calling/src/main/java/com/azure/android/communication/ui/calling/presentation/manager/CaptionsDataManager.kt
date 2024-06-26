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

    private var lastReceivedCaptionsData: CallCompositeCaptionsData? = null

    fun start(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            callingService.getCaptionsReceivedSharedFlow().collect {
                // If the active caption language is not empty and the received caption language is empty, then return
                // as only data with the active caption language should be displayed.
                if (appStore.getCurrentState().captionsState.activeCaptionLanguage.isNotEmpty() &&
                    it.captionLanguage?.isEmpty() == true
                ) {
                    return@collect
                }

                if (captionsDataCache.size >= CallingFragment.MAX_CAPTIONS_DATA_SIZE) {
                    captionsDataCache.removeAt(0)
                }
                var languageCode = ""
                val captionText = if (it.captionText?.isEmpty() == false) {
                    languageCode = it.captionLanguage ?: ""
                    it.captionText
                } else {
                    languageCode = it.spokenLanguage
                    it.spokenText
                }

                if (lastReceivedCaptionsData != null && lastReceivedCaptionsData?.speakerRawId == it.speakerRawId &&
                    lastReceivedCaptionsData?.resultType != CaptionsResultType.FINAL
                ) {
                    lastReceivedCaptionsData = it
                    val data = CaptionsDataViewModel(
                        it.speakerName,
                        captionText,
                        it.speakerRawId,
                        languageCode
                    )
                    captionsLastDataUpdatedStateFlow.value = data
                    captionsDataCache[captionsDataCache.size - 1] = data
                } else {
                    lastReceivedCaptionsData = it
                    val data = CaptionsDataViewModel(
                        it.speakerName,
                        captionText,
                        it.speakerRawId,
                        languageCode
                    )
                    captionsNewDataStateFlow.value = data
                    captionsDataCache.add(data)
                }
            }
        }
    }
}
