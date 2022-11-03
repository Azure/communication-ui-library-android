// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Provides navigation bar view data to Call Composite including title and subtitle.
 *
 * Create an instance of {@link CallCompositeSetupScreenViewData} and pass it to
 * {@link CallCompositeLocalOptions} when launching a new call.
 *
 */
public final class CallCompositeSetupScreenViewData {
    private String title = null;
    private String subtitle = null;

    /**
     * Constructs {@link  CallCompositeSetupScreenViewData}.
     */
    public CallCompositeSetupScreenViewData() {
    }

    /**
     * Get the call title.
     * @return The title of the call.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the call subtitle.
     * @return The subtitle of the call.
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * Set the title of the call setup screen to the supplied String.
     * @param title title of the call.
     * @return The current {@link CallCompositeSetupScreenViewData}.
     */
    public CallCompositeSetupScreenViewData setTitle(final String title) {
        this.title = title;
        return this;
    }

    /**
     * <p>Set subtitle of the call setup screen to the supplied String.</p>
     * <p>Title is required to be set as well for subtitle to appear on the screen.</p>
     *
     * @param subtitle subtitle of the call.
     * @return The current {@link CallCompositeSetupScreenViewData}.
     */
    public CallCompositeSetupScreenViewData setSubtitle(final String subtitle) {
        this.subtitle = subtitle;
        return this;
    }
}
