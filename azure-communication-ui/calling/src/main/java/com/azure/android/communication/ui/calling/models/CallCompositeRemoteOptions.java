// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.ui.calling.CallComposite;

/**
 * CallCompositeRemoteOptions for CallComposite.launch.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the CallCompositeRemoteOptions with {@link CommunicationTokenCredential}
 * {@link CallCompositeJoinLocator}
 * CallCompositeRemoteOptions remoteOptions = new CallCompositeRemoteOptions&#40;
 *     locator, communicationTokenCredential, displayName&#41;
 *
 * &#47;&#47; Launch call
 * callComposite.launch&#40;.., remoteOptions&#41
 * </pre>
 *
 * @see CallComposite
 */
public final class CallCompositeRemoteOptions {
    // Mandatory
    private final CommunicationTokenCredential credential;
    private CallCompositeJoinLocator locator;

    private CallCompositeStartCallOptions startCallOptions;

    // Optional
    private final String displayName;

    /**
     * Create {@link CallCompositeRemoteOptions}.
     *
     * @param locator {@link CallCompositeJoinLocator}.
     * @param credential {@link CommunicationTokenCredential}.
     */
    public CallCompositeRemoteOptions(
            final CallCompositeJoinLocator locator,
            final CommunicationTokenCredential credential) {
        this(locator, credential, "");
    }

    /**
     * Create {@link CallCompositeRemoteOptions}.
     *
     * @param locator {@link CallCompositeJoinLocator}.
     * @param credential {@link CommunicationTokenCredential}.
     * @param displayName User display name other call participants will see.
     */
    public CallCompositeRemoteOptions(
            final CallCompositeJoinLocator locator,
            final CommunicationTokenCredential credential,
            final String displayName) {

        this.credential = credential;
        this.displayName = displayName;
        this.locator = locator;
    }

    public CallCompositeRemoteOptions(
            final CommunicationTokenCredential credential,
            final CallCompositeStartCallOptions startCallOptions,
            final String displayName) {
        this.credential = credential;
        this.startCallOptions = startCallOptions;
        this.displayName = displayName;
    }

    /**
     * Get {@link CommunicationTokenCredential}.
     *
     * @return {@link String}.
     */
    public CommunicationTokenCredential getCredential() {
        return credential;
    }

    /**
     * Get user display name.
     *
     * @return {@link String}.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get call locator.
     *
     * @return {@link CallCompositeJoinLocator}.
     */
    public CallCompositeJoinLocator getLocator() {
        return locator;
    }

    /**
     * Get call options.
     *
     * @return  {@link CallCompositeStartCallOptions}.
     */
    public CallCompositeStartCallOptions getStartCallOptions() {
        return startCallOptions;
    }
}
