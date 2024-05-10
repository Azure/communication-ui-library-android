// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

internal fun createCallCompositeParticipantCapability(
    participantCapabilityType: CallCompositeParticipantCapabilityType,
    isAllowed: Boolean,
    capabilityResolutionReason: CallCompositeCapabilityResolutionReason,
) =
    CallCompositeParticipantCapability(
        participantCapabilityType,
        isAllowed,
        capabilityResolutionReason
    )