// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * The options for the composite call captions.
 */
public final class CallCompositeCaptionsOptions {
    private Boolean captionsOn;
    private String spokenLanguage;

    /**
     * Set the spoken language.
     * @param spokenLanguage The spoken language.
     * @return The {@link CallCompositeCaptionsOptions} object itself.
     */
    public CallCompositeCaptionsOptions setSpokenLanguage(final String spokenLanguage) {
        this.spokenLanguage = spokenLanguage;
        return this;
    }

    /**
     * Get the spoken language.
     * @return The spoken language.
     */
    public String getSpokenLanguage() {
        return spokenLanguage;
    }

    /**
     * Get the captions on.
     * @return The captions on.
     */
    public Boolean isCaptionsOn() {
        return captionsOn;
    }

    /**
     * Set the captions on.
     * @param captionsOn The captions on.
     * @return The {@link CallCompositeCaptionsOptions} object itself.
     */
    public CallCompositeCaptionsOptions setCaptionsOn(final Boolean captionsOn) {
        this.captionsOn = captionsOn;
        return this;
    }
}
