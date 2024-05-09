// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Represents notification mode for user capabilities changed.
 */
public class CallCompositeCapabilitiesChangeNotificationMode
        extends ExpandableStringEnum<CallCompositeCapabilitiesChangeNotificationMode> {
    /**
     * Always display a notification when user capabilities are changed.
     */
    public static final CallCompositeCapabilitiesChangeNotificationMode ALWAYS_DISPLAY = fromString("always_display");

    /**
     * Never display a notification when user capabilities are changed.
     */
    public static final CallCompositeCapabilitiesChangeNotificationMode NEVER_DISPLAY = fromString("never_display");

    /**
     * Creates an instance of {@link CallCompositeCapabilitiesChangeNotificationMode} from a string name.
     *
     * @param name The name of the mode as a string.
     * @return An instance of {@link CallCompositeCapabilitiesChangeNotificationMode}
     * corresponding to the provided name.
     */
    public static CallCompositeCapabilitiesChangeNotificationMode fromString(final String name) {
        return fromString(name, CallCompositeCapabilitiesChangeNotificationMode.class);
    }

    /**
     * Returns all the available values for {@link CallCompositeCapabilitiesChangeNotificationMode}.
     *
     * @return A collection of all {@link CallCompositeCapabilitiesChangeNotificationMode} values.
     */
    public static Collection<CallCompositeCapabilitiesChangeNotificationMode> values() {
        return values(CallCompositeCapabilitiesChangeNotificationMode.class);
    }
}
