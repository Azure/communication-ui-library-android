// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallComposite;

/**
 * CallCompositeClientOptions for CallComposite.launch.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the LocalSettings with {@link CallCompositeParticipantViewData}
 * CallCompositeClientOptions clientOptions = new CallCompositeClientOptions(
 *     new CallCompositeParticipantViewData&#40;...&#41);
 *
 * &#47;&#47; Launch call
 * callComposite.launch&#40; .., .., clientOptions&#41
 * </pre>
 *
 * @see CallComposite
 */
public final class CallCompositeClientOptions {
    private final CallCompositeParticipantViewData participantViewData;

    /**
     * Create LocalSettings.
     *
     * @param participantViewData The {@link CallCompositeParticipantViewData};
     * @see CallCompositeParticipantViewData
     */
    public CallCompositeClientOptions(final CallCompositeParticipantViewData participantViewData) {
        this.participantViewData = participantViewData;
    }

    /**
     * Get {@link CallCompositeParticipantViewData}.
     *
     * @return The {@link CallCompositeParticipantViewData};
     */
    public CallCompositeParticipantViewData getParticipantViewData() {
        return participantViewData;
    }
}
