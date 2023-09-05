// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallComposite;

/**
 * One to one call locator to dial participant {@link CallComposite}.
 */
public final class CallCompositeParticipantDialLocator extends CallCompositeJoinLocator {

    private final String mri;

    /**
     * Creates {@link CallCompositeParticipantDialLocator}.
     *
     * @param mri ParticipantMri, for more information please check Quickstart Doc.
     */
    public CallCompositeParticipantDialLocator(final String mri) {
        this.mri = mri;
    }

    /**
     * Get Teams meeting link.
     *
     * @return {@link String}.
     */
    public String getMri() {
        return mri;
    }
}
