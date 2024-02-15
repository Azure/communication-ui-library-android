// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallComposite;

/**
 * Localization configuration to provide for {@link CallComposite}.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder with parameters
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;
 *     .multitasking&#40;new CallCompositeMultitaskingOptions&#40;true, true&#41;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * </pre>
 *
 * @see CallComposite
 */
public final class CallCompositeMultitaskingOptions {
    private final Boolean enableMultitasking;
    private final Boolean enableSystemPictureInPictureWhenMultitasking;

    /**
     * Creates {@link CallCompositeMultitaskingOptions}.
     * @param enableMultitasking While on the call, user can go back to previous activity from the call composite.
     */
    public CallCompositeMultitaskingOptions(final Boolean enableMultitasking) {
        this.enableMultitasking = enableMultitasking;
        this.enableSystemPictureInPictureWhenMultitasking = false;
    }

    /**
     * Creates {@link CallCompositeMultitaskingOptions}.
     * @param enableMultitasking While on the call, user can go back to previous activity from the call composite.
     * @param enableSystemPictureInPictureWhenMultitasking When enableMultitasking is set to true,
     *                                                     enables a system Picture-in-picture mode when user
     *                                                     navigates away from call composite.
     */
    public CallCompositeMultitaskingOptions(
            final Boolean enableMultitasking, final Boolean enableSystemPictureInPictureWhenMultitasking) {
        this.enableMultitasking = enableMultitasking;

        this.enableSystemPictureInPictureWhenMultitasking = enableMultitasking
                ? enableSystemPictureInPictureWhenMultitasking : false;
    }

    /**
     * Is multitasking enabled.
     * @return {@link Boolean}
     */
    public Boolean isMultitaskingEnabled() {
        return enableMultitasking;
    }

    /**
     * Is Picture-In-Picture enabled.
     * @return {@link Boolean}
     */
    public Boolean isSystemPictureInPictureEnable() {
        return enableSystemPictureInPictureWhenMultitasking;
    }
}
