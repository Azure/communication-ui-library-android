// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

internal enum class CallCompositeCaptionsErrors {
    NONE,
    CAPTIONS_NOT_ACTIVE,
    GET_CAPTIONS_FAILED_CALL_STATE_NOT_CONNECTED,
    CAPTIONS_FAILED_TO_START,
    CAPTIONS_FAILED_TO_STOP,
    CAPTIONS_FAILED_TO_SET_SPOKEN_LANGUAGE,
    FAILED_TO_SET_CAPTION_LANGUAGE,
    CAPTIONS_POLICY_DISABLED,
    CAPTIONS_DISABLED_BY_CONFIGURATIONS,
    CAPTIONS_SET_SPOKEN_LANGUAGE_DISABLED,
    SET_CAPTION_LANGUAGE_DISABLED,
    SET_CAPTION_LANGUAGE_TEAMS_PREMIUM_LICENSE_NEEDED,
    CAPTIONS_REQUESTED_LANGUAGE_NOT_SUPPORTED
}

internal fun com.azure.android.communication.calling.CallingCommunicationErrors.into(): CallCompositeCaptionsErrors {
    return when (this) {
        com.azure.android.communication.calling.CallingCommunicationErrors.CAPTIONS_NOT_ACTIVE -> CallCompositeCaptionsErrors.CAPTIONS_NOT_ACTIVE
        com.azure.android.communication.calling.CallingCommunicationErrors.GET_CAPTIONS_FAILED_CALL_STATE_NOT_CONNECTED -> CallCompositeCaptionsErrors.GET_CAPTIONS_FAILED_CALL_STATE_NOT_CONNECTED
        com.azure.android.communication.calling.CallingCommunicationErrors.CAPTIONS_FAILED_TO_START -> CallCompositeCaptionsErrors.CAPTIONS_FAILED_TO_START
        com.azure.android.communication.calling.CallingCommunicationErrors.CAPTIONS_FAILED_TO_STOP -> CallCompositeCaptionsErrors.CAPTIONS_FAILED_TO_STOP
        com.azure.android.communication.calling.CallingCommunicationErrors.CAPTIONS_FAILED_TO_SET_SPOKEN_LANGUAGE -> CallCompositeCaptionsErrors.CAPTIONS_FAILED_TO_SET_SPOKEN_LANGUAGE
        com.azure.android.communication.calling.CallingCommunicationErrors.FAILED_TO_SET_CAPTION_LANGUAGE -> CallCompositeCaptionsErrors.FAILED_TO_SET_CAPTION_LANGUAGE
        com.azure.android.communication.calling.CallingCommunicationErrors.CAPTIONS_POLICY_DISABLED -> CallCompositeCaptionsErrors.CAPTIONS_POLICY_DISABLED
        com.azure.android.communication.calling.CallingCommunicationErrors.CAPTIONS_DISABLED_BY_CONFIGURATIONS -> CallCompositeCaptionsErrors.CAPTIONS_DISABLED_BY_CONFIGURATIONS
        com.azure.android.communication.calling.CallingCommunicationErrors.CAPTIONS_SET_SPOKEN_LANGUAGE_DISABLED -> CallCompositeCaptionsErrors.CAPTIONS_SET_SPOKEN_LANGUAGE_DISABLED
        com.azure.android.communication.calling.CallingCommunicationErrors.SET_CAPTION_LANGUAGE_DISABLED -> CallCompositeCaptionsErrors.SET_CAPTION_LANGUAGE_DISABLED
        com.azure.android.communication.calling.CallingCommunicationErrors.SET_CAPTION_LANGUAGE_TEAMS_PREMIUM_LICENSE_NEEDED -> CallCompositeCaptionsErrors.SET_CAPTION_LANGUAGE_TEAMS_PREMIUM_LICENSE_NEEDED
        com.azure.android.communication.calling.CallingCommunicationErrors.CAPTIONS_REQUESTED_LANGUAGE_NOT_SUPPORTED -> CallCompositeCaptionsErrors.CAPTIONS_REQUESTED_LANGUAGE_NOT_SUPPORTED
        else -> CallCompositeCaptionsErrors.NONE
    }
}
