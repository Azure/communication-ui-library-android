// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

import com.azure.android.communication.ui.chat.ChatComposite;

/**
 * ChatCompositeLocalOptions for ChatComposite.launch.
 *
 * <p>
 *     Local Options for the Chat Composite. These options are not shared with the server and impact local views only.
 *     E.g. The Local Participant Name if it differs from the display name you'd like to share with the server.
 * </p>
 * <pre>
 *
 * &#47;&#47; Initialize the chat composite builder
 * final ChatCompositeBuilder builder = new ChatCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the chat composite
 * ChatComposite chatComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the ChatCompositeLocalOptions with {@link ChatCompositeParticipantViewData}
 * ChatCompositeLocalOptions localOptions = new ChatCompositeLocalOptions(
 *     new ChatCompositeParticipantViewData&#40;...&#41);
 *
 * &#47;&#47; Launch chat
 * chatComposite.launch&#40;.., .., localOptions&#41
 * </pre>
 *
 * @see ChatComposite
 */
public final class ChatCompositeLocalOptions {
    private boolean isLaunchingWithUI = true;
    private ChatCompositeParticipantViewData participantViewData;

    /**
     * Create Local Options with default setting.
     */
    public ChatCompositeLocalOptions() {

    }

    /**
     * Create Local Options.
     *
     * @param participantViewData The {@link ChatCompositeParticipantViewData};
     * @see ChatCompositeParticipantViewData
     */
    public ChatCompositeLocalOptions(final ChatCompositeParticipantViewData participantViewData) {
        this.participantViewData = participantViewData;
    }

    /**
     * Get {@link ChatCompositeParticipantViewData}.
     *
     * @return The {@link ChatCompositeParticipantViewData};
     */
    public ChatCompositeParticipantViewData getParticipantViewData() {
        return participantViewData;
    }

    /**
     * Get is launching with UI boolean
     *
     * @return boolean isLaunchingWithUI;
     */
    public boolean getIsLaunchingWithUI() {
        return isLaunchingWithUI;
    }

    /**
     * Set is launching with UI boolean
     *
     * @return The {@link ChatCompositeLocalOptions };
     */
    public ChatCompositeLocalOptions setLaunchingWithUI(final boolean isLaunchingWithUI) {
        this.isLaunchingWithUI = isLaunchingWithUI;
        return this;
    }
}
