// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.common.CommunicationIdentifier;
import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.ui.calling.CallComposite;

import java.util.Collection;

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
    private Collection<CommunicationIdentifier> participants;
    private CallCompositePushNotificationInfo pushNotificationInfo;
    private final Boolean disableInternalPushForIncomingCall;

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
        this(locator, credential, "", false);
    }

    /**
     * Create {@link CallCompositeRemoteOptions}.
     *
     * @param locator {@link CallCompositeJoinLocator}.
     * @param credential {@link CommunicationTokenCredential}.
     * @param displayName User display name other call participants will see.
     * @param disableInternalPushForIncomingCall Disable internal push for incoming call.
     */
    public CallCompositeRemoteOptions(
            final CallCompositeJoinLocator locator,
            final CommunicationTokenCredential credential,
            final String displayName,
            final Boolean disableInternalPushForIncomingCall) {
        this.credential = credential;
        this.displayName = displayName;
        this.locator = locator;
        this.disableInternalPushForIncomingCall = disableInternalPushForIncomingCall;
    }

    /**
     * Create {@link CallCompositeRemoteOptions}.
     *
     * @param info {@link CallCompositePushNotificationInfo}.
     * @param credential {@link CommunicationTokenCredential}.
     * @param displayName User display name other call participants will see.
     * @param disableInternalPushForIncomingCall Disable internal push for incoming call.
     */
    public CallCompositeRemoteOptions(
            final CallCompositePushNotificationInfo info,
            final CommunicationTokenCredential credential,
            final String displayName,
            final Boolean disableInternalPushForIncomingCall) {
        this.credential = credential;
        this.pushNotificationInfo = info;
        this.displayName = displayName;
        this.disableInternalPushForIncomingCall = disableInternalPushForIncomingCall;
    }

    /**
     * Create {@link CallCompositeRemoteOptions}.
     *
     * @param info {@link CallCompositePushNotificationInfo}.
     * @param credential {@link CommunicationTokenCredential}.
     * @param disableInternalPushForIncomingCall Disable internal push for incoming call.
     */
    public CallCompositeRemoteOptions(
            final CallCompositePushNotificationInfo info,
            final CommunicationTokenCredential credential,
            final Boolean disableInternalPushForIncomingCall) {
        this.credential = credential;
        this.pushNotificationInfo = info;
        this.displayName = "";
        this.disableInternalPushForIncomingCall = disableInternalPushForIncomingCall;
    }

    /**
     * Create {@link CallCompositeRemoteOptions}.
     *
     * @param participants raw ids {@link Iterable}.
     * @param credential {@link CommunicationTokenCredential}.
     * @param displayName User display name other call participants will see.
     * @param disableInternalPushForIncomingCall Disable internal push for incoming call.
     */
    public CallCompositeRemoteOptions(
            final Collection<CommunicationIdentifier> participants,
            final CommunicationTokenCredential credential,
            final String displayName,
            final Boolean disableInternalPushForIncomingCall) {
        this.credential = credential;
        this.displayName = displayName;
        this.participants = participants;
        this.disableInternalPushForIncomingCall = disableInternalPushForIncomingCall;
    }

    /**
     * Create {@link CallCompositeRemoteOptions}.
     *
     * @param participants raw ids {@link Iterable}.
     * @param credential {@link CommunicationTokenCredential}.
     * @param disableInternalPushForIncomingCall Disable internal push for incoming call.
     */
    public CallCompositeRemoteOptions(
            final Collection<CommunicationIdentifier> participants,
            final CommunicationTokenCredential credential,
            final Boolean disableInternalPushForIncomingCall) {
        this.credential = credential;
        this.displayName = "";
        this.participants = participants;
        this.disableInternalPushForIncomingCall = disableInternalPushForIncomingCall;
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
     * Get participants raw ids.
     *
     * @return {@link Collection} of {@link CommunicationIdentifier}.
     */
    public Collection<CommunicationIdentifier> getParticipants() {
        return participants;
    }

    /**
     * Get push notification info.
     *
     * @return  {@link CallCompositePushNotificationInfo}.
     */
    public CallCompositePushNotificationInfo getPushNotificationInfo() {
        return pushNotificationInfo;
    }

    /**
     * Get disable internal push for incoming call.
     *
     * @return  {@link Boolean}.
     */
    public Boolean getDisableInternalPushForIncomingCall() {
        return disableInternalPushForIncomingCall;
    }
}
