// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <CAPTIONS_RECEIVED> */
package com.azure.android.communication.ui.calling.models;

/**
 * Call composite captions received event.
 */
public class CallCompositeCaptionsReceivedEvent {
    private final String speakerName;
    private final String speakerRawId;
    private final String captionText;
    private final String languageCode;

    CallCompositeCaptionsReceivedEvent(
            final String speakerName,
            final String speakerRawId,
            final String captionText,
            final String languageCode) {
        this.speakerName = speakerName;
        this.speakerRawId = speakerRawId;
        this.captionText = captionText;
        this.languageCode = languageCode;
    }

    /**
     * Get speaker name.
     * @return Speaker name
     */
    public String getSpeakerName() { return speakerName; }

    /**
     * Get speaker raw id.
     * @return Speaker raw id
     */
    public String getSpeakerRawId() { return speakerRawId; }

    /**
     * Get caption text.
     * @return Caption text
     */
    public String getCaptionText() { return captionText; }

    /**
     * Get language code.
     * @return Language code
     */
    public String getLanguageCode() { return languageCode; }
}
/* </CAPTIONS_RECEIVED> */
