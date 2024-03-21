// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Represents the leave call confirmation options for a call composite.
 * This class defines the options for the leave call confirmation, such as enable or disable.
 */
public final class CallCompositeLeaveCallConfirmation extends ExpandableStringEnum<CallCompositeLeaveCallConfirmation> {

    /**
     * Enables the leave call confirmation.
     */
    public static final CallCompositeLeaveCallConfirmation ENABLE = fromString("enable");

    /**
     * Disables the leave call confirmation.
     */
    public static final CallCompositeLeaveCallConfirmation DISABLE = fromString("disable");

    /**
     * Creates an instance of {@link CallCompositeLeaveCallConfirmation} from a string name.
     *
     * @param name The name of the mode as a string.
     * @return An instance of {@link CallCompositeLeaveCallConfirmation} corresponding to the provided name.
     */
    public static CallCompositeLeaveCallConfirmation fromString(final String name) {
        return fromString(name, CallCompositeLeaveCallConfirmation.class);
    }

    /**
     * Returns all the available values for {@link CallCompositeLeaveCallConfirmation}.
     *
     * @return A collection of all {@link CallCompositeLeaveCallConfirmation} values.
     */
    public static Collection<CallCompositeLeaveCallConfirmation> values() {
        return values(CallCompositeLeaveCallConfirmation.class);
    }
}
