// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.persona;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * Defines values for SetPersonaDataResult.
 */
public final class SetPersonaDataResult extends ExpandableStringEnum<SetPersonaDataResult> {
    public static final SetPersonaDataResult SUCCESS = fromString("success");
    public static final SetPersonaDataResult PARTICIPANT_NOT_IN_CALL = fromString("participantNotInCall");

    /**
     * Creates or finds a SetPersonaDataResult from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding SetPersonaDataResult.
     */
    private static SetPersonaDataResult fromString(final String name) {
        return fromString(name, SetPersonaDataResult.class);
    }

    /**
     * @return known SetPersonaDataResult values.
     */
    public static Collection<SetPersonaDataResult> values() {
        return values(SetPersonaDataResult.class);
    }
}
