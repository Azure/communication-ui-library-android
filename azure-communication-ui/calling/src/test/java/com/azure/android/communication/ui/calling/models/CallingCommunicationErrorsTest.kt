// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

import com.azure.android.communication.calling.CallingCommunicationErrors
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.junit.Assert.assertEquals
import org.mockito.Mockito

@RunWith(MockitoJUnitRunner::class)
internal class CallingCommunicationErrorsTest {
    @Test
    fun callingCommunicationErrors_into_conversion() {
        assertEquals(
            CallCompositeCaptionsErrors.CAPTIONS_NOT_ACTIVE,
            CallingCommunicationErrors.CAPTIONS_NOT_ACTIVE.into()
        )

        assertEquals(
            CallCompositeCaptionsErrors.GET_CAPTIONS_FAILED_CALL_STATE_NOT_CONNECTED,
            CallingCommunicationErrors.GET_CAPTIONS_FAILED_CALL_STATE_NOT_CONNECTED.into()
        )

        assertEquals(
            CallCompositeCaptionsErrors.CAPTIONS_FAILED_TO_START,
            CallingCommunicationErrors.CAPTIONS_FAILED_TO_START.into()
        )

        assertEquals(
            CallCompositeCaptionsErrors.CAPTIONS_FAILED_TO_STOP,
            CallingCommunicationErrors.CAPTIONS_FAILED_TO_STOP.into()
        )

        assertEquals(
            CallCompositeCaptionsErrors.CAPTIONS_FAILED_TO_SET_SPOKEN_LANGUAGE,
            CallingCommunicationErrors.CAPTIONS_FAILED_TO_SET_SPOKEN_LANGUAGE.into()
        )

        assertEquals(
            CallCompositeCaptionsErrors.FAILED_TO_SET_CAPTION_LANGUAGE,
            CallingCommunicationErrors.FAILED_TO_SET_CAPTION_LANGUAGE.into()
        )

        assertEquals(
            CallCompositeCaptionsErrors.CAPTIONS_POLICY_DISABLED,
            CallingCommunicationErrors.CAPTIONS_POLICY_DISABLED.into()
        )

        assertEquals(
            CallCompositeCaptionsErrors.CAPTIONS_DISABLED_BY_CONFIGURATIONS,
            CallingCommunicationErrors.CAPTIONS_DISABLED_BY_CONFIGURATIONS.into()
        )

        assertEquals(
            CallCompositeCaptionsErrors.CAPTIONS_SET_SPOKEN_LANGUAGE_DISABLED,
            CallingCommunicationErrors.CAPTIONS_SET_SPOKEN_LANGUAGE_DISABLED.into()
        )

        assertEquals(
            CallCompositeCaptionsErrors.SET_CAPTION_LANGUAGE_DISABLED,
            CallingCommunicationErrors.SET_CAPTION_LANGUAGE_DISABLED.into()
        )

        assertEquals(
            CallCompositeCaptionsErrors.SET_CAPTION_LANGUAGE_TEAMS_PREMIUM_LICENSE_NEEDED,
            CallingCommunicationErrors.SET_CAPTION_LANGUAGE_TEAMS_PREMIUM_LICENSE_NEEDED.into()
        )

        assertEquals(
            CallCompositeCaptionsErrors.CAPTIONS_REQUESTED_LANGUAGE_NOT_SUPPORTED,
            CallingCommunicationErrors.CAPTIONS_REQUESTED_LANGUAGE_NOT_SUPPORTED.into()
        )

        val unknownError = Mockito.mock(CallingCommunicationErrors::class.java)
        assertEquals(CallCompositeCaptionsErrors.NONE, unknownError.into())
    }
}
