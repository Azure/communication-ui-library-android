// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Represents the leave call confirmation options for a call composite.
 * This class defines the options for the leave call confirmation, such as enable or disable.
 */
public final class CallCompositeLeaveCallConfirmationMode extends ExpandableStringEnum<CallCompositeLeaveCallConfirmationMode> {

    /**
     * Enables the leave call confirmation.
     */
    public static final CallCompositeLeaveCallConfirmationMode ALWAYS_ENABLED = fromString("always_enable");

    /**
     * Disables the leave call confirmation.
     */
    public static final CallCompositeLeaveCallConfirmationMode ALWAYS_DISABLED = fromString("always_disable");

    /**
     * Creates an instance of {@link CallCompositeLeaveCallConfirmationMode} from a string name.
     *
     * @param name The name of the mode as a string.
     * @return An instance of {@link CallCompositeLeaveCallConfirmationMode} corresponding to the provided name.
     */
    public static CallCompositeLeaveCallConfirmationMode fromString(final String name) {
        return fromString(name, CallCompositeLeaveCallConfirmationMode.class);
    }

    /**
     * Returns all the available values for {@link CallCompositeLeaveCallConfirmationMode}.
     *
     * @return A collection of all {@link CallCompositeLeaveCallConfirmationMode} values.
     */
    public static Collection<CallCompositeLeaveCallConfirmationMode> values() {
        return values(CallCompositeLeaveCallConfirmationMode.class);
    }
}
