// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchat.models;

/**
 * Call With Chat locator
 */
public abstract class CallWithChatCompositeJoinLocator {

    protected final String endpoint;

    protected CallWithChatCompositeJoinLocator(final String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
