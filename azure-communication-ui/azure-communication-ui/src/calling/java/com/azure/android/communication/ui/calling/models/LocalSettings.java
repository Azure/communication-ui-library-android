// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallComposite;

/**
 * LocalSettings for CallComposite.launch.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the LocalSettings with {@link ParticipantViewData}
 * LocalSettings localSettings = new LocalSettings(new ParticipantViewData&#40;...&#41);
 *
 * &#47;&#47; Launch call
 * callComposite.launch&#40; .., .., localSettings&#41
 * </pre>
 *
 * @see CallComposite
 */
public final class LocalSettings {
    private final ParticipantViewData participantViewData;

    /**
     * Create LocalSettings.
     *
     * @param participantViewData The {@link ParticipantViewData};
     * @see ParticipantViewData
     */
    public LocalSettings(final ParticipantViewData participantViewData) {
        this.participantViewData = participantViewData;
    }

    /**
     * Get ParticipantViewData
     *
     * @return The {@link ParticipantViewData};
     */
    public ParticipantViewData getParticipantViewData() {
        return participantViewData;
    }
}
