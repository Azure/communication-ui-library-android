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
    @Nullable
    private CallCompositeParticipantViewData participantViewData = null;

    @Nullable
    private CallCompositeNavigationBarViewData navigationBarViewData = null;


    /**
     * Create LocalSettings.
     *
     * @param participantViewData The {@link CallCompositeParticipantViewData};
     * @see CallCompositeParticipantViewData
     */
    public CallCompositeLocalOptions(final CallCompositeParticipantViewData participantViewData) {
        this.participantViewData = participantViewData;
    }

    /**
     * Create an empty {@Link CallCompositeLocalOptions} object and assign using setters
     */
    public CallCompositeLocalOptions() { }

    /**
     * Get the {@link CallCompositeParticipantViewData}
     *
     * @return The {@link CallCompositeParticipantViewData};
     */
    @Nullable
    public CallCompositeParticipantViewData getParticipantViewData() {
        return participantViewData;
    }

    /**
     * Set a {@linnk CallCompositeParticipantViewData} to be used
     * @param participantViewData
     */
    public void setParticipantViewData(@Nullable final CallCompositeParticipantViewData participantViewData) {
        this.participantViewData = participantViewData;
    }

    /**
     * Get the {@link CallCompositeNavigationBarViewData}
     * @return
     */
    @Nullable
    public CallCompositeNavigationBarViewData getNavigationBarViewData() {
        return navigationBarViewData;
    }

    /**
     * Set a {@link CallCompositeNavigationBarViewData} to be used
     * @param navigationBarViewData
     */
    public void setNavigationBarViewData(@Nullable final CallCompositeNavigationBarViewData navigationBarViewData) {
        this.navigationBarViewData = navigationBarViewData;
    }
}
