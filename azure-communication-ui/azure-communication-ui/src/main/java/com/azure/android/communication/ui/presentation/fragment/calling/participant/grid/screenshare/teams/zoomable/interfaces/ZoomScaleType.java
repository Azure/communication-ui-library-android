package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.teams.zoomable.interfaces;

import static com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.teams.zoomable.interfaces.ZoomScaleType.CENTER;
import static com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.teams.zoomable.interfaces.ZoomScaleType.FIT_INSIDE;
import static com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.teams.zoomable.interfaces.ZoomScaleType.ZOOM_TO_FIT;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef(value = {CENTER, FIT_INSIDE, ZOOM_TO_FIT})
@Retention(RetentionPolicy.SOURCE)
public @interface ZoomScaleType {
    int CENTER = 0;
    int FIT_INSIDE = 1;
    int ZOOM_TO_FIT = 2;
}