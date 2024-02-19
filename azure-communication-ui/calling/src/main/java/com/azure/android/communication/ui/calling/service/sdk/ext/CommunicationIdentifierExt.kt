// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk.ext

import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier
import com.azure.android.communication.common.PhoneNumberIdentifier
import com.azure.android.communication.common.UnknownIdentifier

internal fun CommunicationIdentifier.id(): String {
    return when (this) {
        is PhoneNumberIdentifier -> this.phoneNumber
        is MicrosoftTeamsUserIdentifier -> this.userId
        is CommunicationUserIdentifier -> this.id
        else -> (this as UnknownIdentifier).id
    }
}
