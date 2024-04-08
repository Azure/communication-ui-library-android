// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.CallCompositeException
import com.azure.android.communication.ui.calling.CallCompositeInstanceManager
import com.azure.android.communication.ui.calling.di.DependencyInjectionContainer
import com.azure.android.communication.ui.calling.getDiContainer
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
internal class DependencyInjectionContainerHolder(
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
            SetupViewModelFactory(container.appStore, application),
            container.networkManager
        )
    }
    val callingViewModel by lazy {
        CallingViewModel(
            container.appStore,
            CallingViewModelFactory(
                container.appStore,
                ParticipantGridCellViewModelFactory(),
                application.resources.getInteger(R.integer.azure_communication_ui_calling_max_remote_participants),
                container.debugInfoManager,
                container.configuration.callCompositeEventsHandler.getOnUserReportedHandlers().toList().isNotEmpty(),
                container.configuration.enableMultitasking
            ),
            container.networkManager,
            container.configuration.enableMultitasking,
            container.configuration.callCompositeLocalOptions?.audioVideoMode
                ?: CallCompositeAudioVideoMode.AUDIO_AND_VIDEO
        )
    }
}
