// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import androidx.annotation.Nullable;

import com.azure.android.communication.ui.calling.CallComposite;

/**
 * CallCompositeLocalOptions for CallComposite.launch.
 *
 * <p>
 *     Local Options for the Call Composite. These options are not shared with the server and impact local views only.
 *     E.g. The Local Participant Name if it differs from the display name you'd like to share with the server.
 * </p>
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the CallCompositeLocalOptions with {@link CallCompositeParticipantViewData}
 * CallCompositeLocalOptions localOptions = new CallCompositeLocalOptions(
 *     new CallCompositeParticipantViewData&#40;...&#41);
 *
 * &#47;&#47; Launch call
 * callComposite.launch&#40;.., .., localOptions&#41
 * </pre>
 *
 * @see CallComposite
 */
public final class CallCompositeLocalOptions {
    private final CallCompositeParticipantViewData participantViewData;

    private final String callTitle;

    private final String callSubTitle;

    /**
     * Create LocalSettings.
     *
     * @param participantViewData The {@link CallCompositeParticipantViewData};
     * @see CallCompositeParticipantViewData
     */
    public CallCompositeLocalOptions(final CallCompositeParticipantViewData participantViewData,
                                     @Nullable final String callTitle, @Nullable final String callSubTitle) {
        this.participantViewData = participantViewData;
        this.callTitle = callTitle;
        this.callSubTitle = callSubTitle;
    }

    /**
     * Get {@link CallCompositeParticipantViewData}.
     *
     * @return The {@link CallCompositeParticipantViewData};
     */
    public CallCompositeParticipantViewData getParticipantViewData() {
        return participantViewData;
    }


    public String getCallTitle() {
        return callTitle;
    }

    public String getCallSubTitle() {
        return callSubTitle;
    }

}
