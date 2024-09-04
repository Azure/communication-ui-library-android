// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallCompositeEventHandler;

import java.util.UUID;

/**
 * Call composite custom button view data.
 */
public final class CallCompositeCustomButtonViewData {
    CallCompositeEventHandler<String> titleChangedEventHandler;
    CallCompositeEventHandler<Integer> drawableIdChangedEventHandler;
    CallCompositeEventHandler<Boolean> enabledChangedEventHandler;
    CallCompositeEventHandler<Boolean> visibleChangedEventHandler;
    private final UUID id;
    private int drawableId;
    private String title;
    private final CallCompositeEventHandler<CallCompositeCustomButtonClickEvent> onClickHandler;
    private Boolean isEnabled = true;
    private Boolean isVisible = true;

    /**
     * Create call composite custom button view data.
     * @param drawableId an icon for the button.
     * @param title a text to be displayed on the button.
     * @param onClickHandler button on click handler.
     */
    public CallCompositeCustomButtonViewData(
            final UUID id,
            final int drawableId,
            final String title,
            final CallCompositeEventHandler<CallCompositeCustomButtonClickEvent> onClickHandler) {
        this.id = id;
        this.drawableId = drawableId;
        this.title = title;
        this.onClickHandler = onClickHandler;
    }

    /**
     * Get button id.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Get an icon for the button.
     */
    public int getDrawableId() {
        return drawableId;
    }

    /**
     * Set drawableId property.
     */
    public CallCompositeCustomButtonViewData setDrawableId(final int drawableId) {
        this.drawableId = drawableId;
        if (drawableIdChangedEventHandler != null) {
            drawableIdChangedEventHandler.handle(drawableId);
        }
        return this;
    }

    /**
     * Get an title for the button.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title property.
     */
    public CallCompositeCustomButtonViewData setTitle(final String title) {
        this.title = title;
        if (titleChangedEventHandler != null) {
            titleChangedEventHandler.handle(title);
        }
        return this;
    }

    /**
     * Get an on click handler for the button.
     */
    public CallCompositeEventHandler<CallCompositeCustomButtonClickEvent> getOnClickHandler() {
        return onClickHandler;
    }

    /**
     * Get isEnabled property.
     */
    public Boolean isEnabled() {
        return this.isEnabled;
    }

    /**
     * Set isEnabled property.
     */
    public CallCompositeCustomButtonViewData setEnabled(final Boolean isEnabled) {
        this.isEnabled = isEnabled;
        if (enabledChangedEventHandler != null) {
            enabledChangedEventHandler.handle(isEnabled);
        }
        return this;
    }

    /**
     * Get isVisible property.
     */
    public Boolean isVisible() {
        return this.isVisible;
    }

    /**
     * Set isVisible property.
     */
    public CallCompositeCustomButtonViewData setVisible(final Boolean isVisible) {
        this.isVisible = isVisible;
        if (visibleChangedEventHandler != null) {
            visibleChangedEventHandler.handle(isVisible);
        }
        return this;
    }
}
