// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Provides navigation bar view data to Call Composite including title and subtitle
 *
 * Create an instance of CallCompositeNavigationBarViewData and pass it to
 * {@link CallCompositeLocalOptions} when launching a new call
 *
 */
public final class CallCompositeNavigationBarViewData {
    private String title = null;
    private String subtitle = null;

    /**
     * Constructs an empty {@link  CallCompositeNavigationBarViewData}
     */
    public CallCompositeNavigationBarViewData(final String title) {
        this.title = title;
    }

    /**
     * Get the call title
     * @return The title of the call
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of the call setup screen to the supplied String
     * @param title Title of the call
     * @return The current CallCompositeNavigationBarViewData for Fluent use
     */
    public CallCompositeNavigationBarViewData setTitle(final String title) {
        this.title = title;
        return this;
    }

    /**
     * Get the call sub title
     * @return The subtitle of the call
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * Set the subtitle of the call setup screen to the supplied String
     * @param subtitle Subtitle of the call
     * @return The current CallCompositeNavigationBarViewData for Fluent use
     */
    public CallCompositeNavigationBarViewData setSubtitle(final String subtitle) {
        this.subtitle = subtitle;
        return this;
    }
}
