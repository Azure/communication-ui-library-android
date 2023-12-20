// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.core.util.ExpandableStringEnum;

import java.util.Collection;

/**
 * A call composite screen orientation.
 *
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder with different parameters
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;
 *     .setupScreenOrientation&#40;CallCompositeSupportedScreenOrientation.PORTRAIT&#41;
 *     .callScreenOrientation&#40;CallCompositeSupportedScreenOrientation.LANDSCAPE&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * </pre>
 */
public final class CallCompositeSupportedScreenOrientation
        extends ExpandableStringEnum<CallCompositeSupportedScreenOrientation> {

    /**
     * To be used when screen needs to be set in portrait mode only.
     */
    public static final CallCompositeSupportedScreenOrientation PORTRAIT =
            fromString("PORTRAIT");
    /**
     * To be used when screen needs to be set in landscape mode only.
     */
    public static final CallCompositeSupportedScreenOrientation LANDSCAPE =
            fromString("LANDSCAPE");
    /**
     * To be used when screen needs to be set in reverse landscape only.
     */
    public static final CallCompositeSupportedScreenOrientation REVERSE_LANDSCAPE =
            fromString("REVERSE_LANDSCAPE");

    /***
     * To be used when screen needs to be in landscape in either direction.
     */
    public static final CallCompositeSupportedScreenOrientation USER_LANDSCAPE = fromString("USER_LANDSCAPE");

    /**
     * To be used when screen needs to be set in full sensor.
     */
    public static final CallCompositeSupportedScreenOrientation FULL_SENSOR =
            fromString("FULL_SENSOR");
    /**
     * To be used when screen needs to be set as per user orientation of the handset.
     */
    public static final CallCompositeSupportedScreenOrientation USER =
            fromString("USER");

    /**
     * Creates or finds a {@link CallCompositeSupportedScreenOrientation} from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding CallCompositeSupportedScreenOrientation.
     */
    public static CallCompositeSupportedScreenOrientation fromString(final String name) {
        return fromString(name, CallCompositeSupportedScreenOrientation.class);
    }

    /**
     * @return known {@link CallCompositeSupportedScreenOrientation} values.
     */
    public static Collection<CallCompositeSupportedScreenOrientation> values() {
        return values(CallCompositeSupportedScreenOrientation.class);
    }
}
