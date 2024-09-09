// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <CUSTOM_CALL_HEADER> */
package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallCompositeEventHandler;

/**
 * Options for the {@link CallCompositeCallScreenHeaderViewData}.
 */
public final class CallCompositeCallScreenHeaderViewData {
    CallCompositeEventHandler<String> titleChangedEventHandler;
    CallCompositeEventHandler<String> subtitleChangedEventHandler;

    private String title;
    private String subtitle;

    /**
     * Create a {@link CallCompositeCallScreenHeaderViewData} object.
     */
    public CallCompositeCallScreenHeaderViewData() {
    }

    /**
     * Set the subtitle.
     *
     * @param subtitle The subtitle.
     * @return The {@link CallCompositeCallScreenHeaderViewData} object itself.
     */
    public CallCompositeCallScreenHeaderViewData setSubtitle(
            final String subtitle) {
        this.subtitle = subtitle;
        if (subtitleChangedEventHandler != null) {
            subtitleChangedEventHandler.handle(subtitle);
        }
        return this;
    }

    /**
     * Get the subtitle.
     *
     * @return {@link String} The subtitle.
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * Set the title.
     *
     * @param title The title.
     * @return The {@link CallCompositeCallScreenHeaderViewData} object itself.
     */
    public CallCompositeCallScreenHeaderViewData setTitle(
            final String title) {
        this.title = title;
        if (titleChangedEventHandler != null) {
            titleChangedEventHandler.handle(title);
        }
        return this;
    }

    /**
     * Get the title.
     *
     * @return {@link String} The title.
     */
    public String getTitle() {
        return title;
    }
}
/* </CUSTOM_CALL_HEADER> */
