/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.teams.zoomable;

import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.teams.zoomable.interfaces.ZoomableController;

/**
 * Interface definition for something which can provide a {@link ZoomableController}.
 */
public interface IZoomableControllerProvider {

    ZoomableController getZoomableController();
}
