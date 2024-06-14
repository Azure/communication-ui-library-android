// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Represents notification mode for user capabilities changed.
 */
public final class CallCompositeCapabilitiesChangedNotificationMode
        extends ExpandableStringEnum<CallCompositeCapabilitiesChangedNotificationMode> {
    /**
     * Always display a notification when user capabilities are changed.
     */
    public static final CallCompositeCapabilitiesChangedNotificationMode ALWAYS_DISPLAY = fromString("always_display");

    /**
     * Never display a notification when user capabilities are changed.
     */
    public static final CallCompositeCapabilitiesChangedNotificationMode NEVER_DISPLAY = fromString("never_display");

    /**
     * Creates an instance of {@link CallCompositeCapabilitiesChangedNotificationMode} from a string name.
     *
     * @param name The name of the mode as a string.
     * @return An instance of {@link CallCompositeCapabilitiesChangedNotificationMode}
     * corresponding to the provided name.
     */
    public static CallCompositeCapabilitiesChangedNotificationMode fromString(final String name) {
        return fromString(name, CallCompositeCapabilitiesChangedNotificationMode.class);
    }

    /**
     * Returns all the available values for {@link CallCompositeCapabilitiesChangedNotificationMode}.
     *
     * @return A collection of all {@link CallCompositeCapabilitiesChangedNotificationMode} values.
     */
    public static Collection<CallCompositeCapabilitiesChangedNotificationMode> values() {
        return values(CallCompositeCapabilitiesChangedNotificationMode.class);
    }
}
