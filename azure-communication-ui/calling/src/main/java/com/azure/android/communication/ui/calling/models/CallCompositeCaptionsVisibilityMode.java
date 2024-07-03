// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * The visibility mode for the composite call captions.
 */
public final class CallCompositeCaptionsVisibilityMode
        extends ExpandableStringEnum<CallCompositeCaptionsVisibilityMode> {

    /**
     * Show captions option.
     */
    public static final CallCompositeCaptionsVisibilityMode SHOW = fromString("show");

    /**
     * Hide captions option.
     */
    public static final CallCompositeCaptionsVisibilityMode HIDE = fromString("hide");

    public static CallCompositeCaptionsVisibilityMode fromString(final String name) {
        return fromString(name, CallCompositeCaptionsVisibilityMode.class);
    }

    public static Collection<CallCompositeCaptionsVisibilityMode> values() {
        return values(CallCompositeCaptionsVisibilityMode.class);
    }
}
