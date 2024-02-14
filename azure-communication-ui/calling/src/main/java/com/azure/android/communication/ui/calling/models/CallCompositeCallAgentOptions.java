// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.content.Context;

import com.azure.android.communication.common.CommunicationTokenCredential;

/**
 * CallCompositePushNotificationOptions for registering device token to receive incoming call.
 */
public final class CallCompositeCallAgentOptions {
    private final CommunicationTokenCredential credential;
    private final String displayName;
    private final CallCompositeTelecomOptions telecomOptions;
    private final Context context;

    /**
     * Create {@link CallCompositeCallAgentOptions}.
     *
     * @param context {@link Context} to be used for creating call agent.
     * @param credential {@link CommunicationTokenCredential} to be used for creating call agent.
     * @param displayName display name.
     * @param telecomOptions telecom options.
     */
    public CallCompositeCallAgentOptions(final Context context,
                                         final CommunicationTokenCredential credential,
                                         final String displayName,
                                         final CallCompositeTelecomOptions telecomOptions) {
        this.credential = credential;
        this.displayName = displayName;
        this.telecomOptions = telecomOptions;
        this.context = context;
    }

    /**
     * Create {@link CallCompositeCallAgentOptions}.
     * @param context {@link Context} to be used for creating call agent.
     * @param credential {@link CommunicationTokenCredential} to be used for creating call agent.
     */
    public CallCompositeCallAgentOptions(final Context context,
                                         final CommunicationTokenCredential credential) {
        this.credential = credential;
        this.displayName = null;
        this.telecomOptions = null;
        this.context = context;
    }

    /**
     * Create {@link CallCompositeCallAgentOptions}.
     * @param context {@link Context} to be used for creating call agent.
     * @param credential {@link CommunicationTokenCredential} to be used for creating call agent.
     * @param displayName display name.
     */
    public CallCompositeCallAgentOptions(final Context context,
                                         final CommunicationTokenCredential credential,
                                         final String displayName) {
        this.credential = credential;
        this.displayName = displayName;
        this.telecomOptions = null;
        this.context = context;
    }

    /**
     * Create {@link CallCompositeCallAgentOptions}.
     * @param context {@link Context} to be used for creating call agent.
     * @param credential {@link CommunicationTokenCredential} to be used for creating call agent.
     * @param telecomOptions telecom options.
     */
    public CallCompositeCallAgentOptions(final Context context,
                                         final CommunicationTokenCredential credential,
                                         final CallCompositeTelecomOptions telecomOptions) {
        this.credential = credential;
        this.displayName = null;
        this.telecomOptions = telecomOptions;
        this.context = context;
    }

    /**
     * Get display name.
     * @return display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get token credential.
     * @return token credential.
     */
    public CommunicationTokenCredential getCredential() {
        return credential;
    }

    /**
     * Get telecom options.
     * @return telecom options.
     */
    public CallCompositeTelecomOptions getTelecomOptions() {
        return telecomOptions;
    }

    /**
     * Get context.
     * @return context.
     */
    public Context getContext() {
        return context;
    }
}
