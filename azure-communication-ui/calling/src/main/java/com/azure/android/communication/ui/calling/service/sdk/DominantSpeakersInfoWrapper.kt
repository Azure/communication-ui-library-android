// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

internal class DominantSpeakersInfoWrapper(
    private val dominantSpeakersInfo: com.azure.android.communication.calling.DominantSpeakersInfo
) : DominantSpeakersInfo {

    var identifiers: List<String>? = null

    override val speakers: List<String>
        get() {
            identifiers?.let { return it }

            val newIdentifiers = dominantSpeakersInfo.speakers.map {
                it.rawId
            }
            identifiers = newIdentifiers
            return newIdentifiers
        }
}
