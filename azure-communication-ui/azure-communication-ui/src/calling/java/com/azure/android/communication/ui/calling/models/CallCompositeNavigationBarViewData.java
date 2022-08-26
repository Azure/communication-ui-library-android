// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Provides navigation bar view data to Call Composite including title and subTitle
 *
 * Create an instance of CallCompositeNavigationBarViewData and pass it to
 * {@link CallCompositeLocalOptions} when launching a new call
 *
 */
public final class CallCompositeNavigationBarViewData {
    private String callTitle = null;

    private String callSubTitle = null;

    /**
     * Constructs an empty {@link  CallCompositeNavigationBarViewData}
     */
    public CallCompositeNavigationBarViewData() { }

    /**
     * Get the call title
     * @return The title of the call
     */
    public String getCallTitle() {
        return callTitle;
    }

    /**
     * Get the call sub title
     * @return The subtitle of the call
     */
    public String getCallSubTitle() {
        return callSubTitle;
    }

    /**
     * Set the title of the call setup screen to the supplied String
     * @param callTitle Title of the call
     * @return The current CallCompositeNavigationBarViewData for Fluent use
     */
    public CallCompositeNavigationBarViewData setCallTitle(final String callTitle) {
        this.callTitle = callTitle;
        return this;
    }

    /**
     * Set the subtitle of the call setup screen to the supplied String
     * @param callSubTitle Subtitle of the call
     * @return The current CallCompositeNavigationBarViewData for Fluent use
     */
    public CallCompositeNavigationBarViewData setCallSubTitle(final String callSubTitle) {
        this.callSubTitle = callSubTitle;
        return this;
    }
}
