// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat.models;

/**
 * Provides navigation bar view data to Call Composite including title and subtitle.
 *
 * Create an instance of {@link CallWithChatCompositeNavigationBarViewData} and pass it to
 * {@link CallWithChatCompositeLocalOptions} when launching a new call.
 *
 */
public final class CallWithChatCompositeNavigationBarViewData {
    private String callTitle = null;
    private String callSubtitle = null;

    /**
     * Get the call title.
     * @return The title of the call.
     */
    public String getCallTitle() {
        return callTitle;
    }

    /**
     * Set the call title of the call setup screen to the supplied String.
     * @param callTitle Title of the call.
     * @return The current {@link CallWithChatCompositeNavigationBarViewData}.
     */
    public CallWithChatCompositeNavigationBarViewData setCallTitle(final String callTitle) {
        this.callTitle = callTitle;
        return this;
    }

    /**
     * Get the call sub title.
     * @return The subtitle of the call.
     */
    public String getCallSubtitle() {
        return callSubtitle;
    }

    /**
     * Set the subtitle of the call setup screen to the supplied String.
     * @param callSubtitle Subtitle of the call.
     * @return The current {@link CallWithChatCompositeNavigationBarViewData}.
     */
    public CallWithChatCompositeNavigationBarViewData setCallSubtitle(final String callSubtitle) {
        this.callSubtitle = callSubtitle;
        return this;
    }
}
