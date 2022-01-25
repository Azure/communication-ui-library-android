// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable;

import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces.ZoomableController;

/**
 * Interface definition for something which can provide a {@link ZoomableController}.
 */
public interface IZoomableControllerProvider {

    ZoomableController getZoomableController();
}
