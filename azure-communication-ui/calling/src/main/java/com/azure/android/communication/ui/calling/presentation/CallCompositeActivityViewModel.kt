// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.azure.android.communication.ui.calling.CallCompositeException
import com.azure.android.communication.ui.calling.CallCompositeInstanceManager
import com.azure.android.communication.ui.calling.di.DependencyInjectionContainer
import com.azure.android.communication.ui.calling.getDiContainer
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.presentation.fragment.calling.CallingViewModel
import com.azure.android.communication.ui.calling.presentation.fragment.factories.CallingViewModelFactory
import com.azure.android.communication.ui.calling.presentation.fragment.factories.ParticipantGridCellViewModelFactory
import com.azure.android.communication.ui.calling.presentation.fragment.factories.SetupViewModelFactory
import com.azure.android.communication.ui.calling.presentation.fragment.setup.SetupViewModel

/**
 * ViewModel for the CallCompositeActivity
 *
 * The instanceID should match the one passed to the Activity via Intent.
 * it needs to be set in activity onCreate() before accessing container or it
 * will throw an exception.
 *
 * Afterwards you can reference container, which holds the services.
 */
internal class CallCompositeActivityViewModel(
    application: Application,
) : AndroidViewModel(application) {
    companion object {
        private const val commonMessage =
            "Please ensure that you have set a valid instanceId before retrieving the container."
    }
    // Instance ID to locate Configuration. -1 is invalid.
    var instanceId: Int = -1
        set(value) {
            if (!CallCompositeInstanceManager.hasCallComposite(value)) {
                val exceptionMessage =
                    "Configuration with instanceId:$value does not exist. $commonMessage"
                throw CallCompositeException(exceptionMessage, IllegalArgumentException(exceptionMessage))
            }
            field = value
        }

    val container: DependencyInjectionContainer by lazy {
        if (instanceId == -1) {
            val exceptionMessage =
                "Will not be able to locate a Configuration for instanceId: -1. $commonMessage"
            throw CallCompositeException(exceptionMessage, IllegalStateException(exceptionMessage))
        }

        return@lazy CallCompositeInstanceManager.getCallComposite(instanceId).getDiContainer()
    }

    val setupViewModel by lazy {
        SetupViewModel(
            container.appStore,
            SetupViewModelFactory(
                container.appStore, application,
                container.configuration.callConfig?.callType,
                isTelecomManagerEnabled = container.configuration.telecomManagerOptions != null,
                logger = container.logger,
            ),
            container.networkManager,
            container.configuration.setupScreenOptions
        )
    }
    val callingViewModel by lazy {
        CallingViewModel(
            store = container.appStore,
            callingViewModelProvider = CallingViewModelFactory(
                store = container.appStore,
                participantGridCellViewModelFactory = ParticipantGridCellViewModelFactory(),
                maxRemoteParticipants = application.resources.getInteger(R.integer.azure_communication_ui_calling_max_remote_participants),
                debugInfoManager = container.debugInfoManager,
                capabilitiesManager = container.capabilitiesManager,
                showSupportFormOption = container.configuration.callCompositeEventsHandler.getOnUserReportedHandlers().any(),
                enableMultitasking = container.configuration.enableMultitasking,
                isTelecomManagerEnabled = container.configuration.telecomManagerOptions != null,
                callType = container.configuration.callConfig.callType,
                callScreenControlBarOptions = container.configuration.callScreenOptions?.controlBarOptions,
                isCaptionsEnabled = container.appStore.getCurrentState().captionsState.isCaptionsUIEnabled,
                callDurationManager = container.callDurationManager,
                customTitle = container.configuration.callScreenOptions?.headerOptions?.title,
                logger = container.logger,
            ),
            networkManager = container.networkManager,
            callScreenOptions = container.configuration.callScreenOptions,
            multitaskingEnabled = container.configuration.enableMultitasking,
            avMode = container.configuration.callCompositeLocalOptions?.audioVideoMode
                ?: CallCompositeAudioVideoMode.AUDIO_AND_VIDEO,
            callType = container.configuration.callConfig.callType,
            capabilitiesManager = container.capabilitiesManager
        )
    }
}
