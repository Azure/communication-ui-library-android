// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

internal data class CapabilitiesChangedEvent(
    val changedCapabilities: List<ParticipantCapability>,
    val capabilitiesChangedReason: CapabilitiesChangedReason
)
