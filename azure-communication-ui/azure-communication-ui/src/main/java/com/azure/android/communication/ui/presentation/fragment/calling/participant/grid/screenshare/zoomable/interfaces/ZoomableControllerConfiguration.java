// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces;

/**
 * Configuration for the Zoomable Controller
 */
interface ZoomableControllerConfiguration {
    /**
     * Gets whether the scale gesture is enabled or not.
     */
    boolean isScaleEnabled();

    /**
     * Sets whether the scale gesture is enabled or not.
     */
    void setScaleEnabled(boolean enabled);

    /**
     * Gets whether the translations gesture is enabled or not.
     */
    boolean isTranslationEnabled();

    /**
     * Sets whether the translation gesture is enabled or not.
     */
    void setTranslationEnabled(boolean enabled);

    /**
     * Gets the minimum scale factor allowed.
     */
    float getMinScaleFactor();

    /**
     * Sets the minimum scale factor allowed.
     * <p> Hierarchy's scaling (if any) is not taken into account.
     */
    void setMinScaleFactor(float minScaleFactor);

    /**
     * Gets the maximum scale factor allowed.
     */
    float getMaxScaleFactor();

    /**
     * Sets the maximum scale factor allowed.
     * <p> Hierarchy's scaling (if any) is not taken into account.
     */
    void setMaxScaleFactor(float maxScaleFactor);
}
