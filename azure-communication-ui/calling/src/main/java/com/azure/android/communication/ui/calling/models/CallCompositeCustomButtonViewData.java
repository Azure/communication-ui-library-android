// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallCompositeEventHandler;

/**
 * Custom button view data.
 */
public final class CallCompositeCustomButtonViewData {

    private String description;
    private int imageResourceId;
    private CallCompositeCustomButtonType customButtonType;
    private Integer badgeNumber;
    private boolean disabled;
    private CallCompositeEventHandler<Object> onClickEventHandler;
    private CallCompositeEventHandler<CallCompositeCustomButtonViewData> onFieldUpdatedListener;

    /**
     * Constructs custom button view data.
     * @param customButtonType Type of the button {@link CallCompositeCustomButtonType}.
     * @param imageResourceId Image resource Id for ImageButton.
     * @param description ImageButton content description.
     * @param onClickEventHandler A button on click listener.
     */
    public CallCompositeCustomButtonViewData(final CallCompositeCustomButtonType customButtonType,
                                             final int imageResourceId,
                                             final String description,
                                             final CallCompositeEventHandler<Object> onClickEventHandler) {

        this.customButtonType = customButtonType;
        this.imageResourceId = imageResourceId;
        this.description = description;
        this.onClickEventHandler = onClickEventHandler;
    }

    /**
     * Get description.
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set description.
     * @param description
     * @return {@link CallCompositeCustomButtonViewData}
     */
    public CallCompositeCustomButtonViewData setDescription(final String description) {
        this.description = description;
        onFieldUpdated();
        return this;
    }

    /**
     * Get image resource Id.
     * @return
     */
    public int getImageResourceId() {
        return imageResourceId;
    }

    /**
     * Set image resource Id.
     * @param imageResourceId
     * @return {@link CallCompositeCustomButtonViewData}
     */
    public CallCompositeCustomButtonViewData setImageResourceId(final int imageResourceId) {
        this.imageResourceId = imageResourceId;
        onFieldUpdated();
        return this;
    }

    /**
     * Get custom button type {@link CallCompositeCustomButtonType}.
     * @return {@link CallCompositeCustomButtonType}
     */
    public CallCompositeCustomButtonType getCustomButtonType() {
        return customButtonType;
    }

    /**
     * Set custom button type.
     * @param customButtonType {@link CallCompositeCustomButtonType}
     * @return {@link CallCompositeCustomButtonViewData}
     */
    public CallCompositeCustomButtonViewData setCustomButtonType(final CallCompositeCustomButtonType customButtonType) {
        this.customButtonType = customButtonType;
        onFieldUpdated();
        return this;
    }

    /**
     * Get badge number.
     * @return
     */
    public Integer getBadgeNumber() {
        return badgeNumber;
    }

    /**
     * Set badge number. Number is displayed over image button indicating number of notifications.
     * @param badgeNumber
     * @return {@link CallCompositeCustomButtonViewData}
     */
    public CallCompositeCustomButtonViewData setBadgeNumber(final Integer badgeNumber) {
        this.badgeNumber = badgeNumber;
        onFieldUpdated();
        return this;
    }

    /**
     * Get is disabled.
     * @return
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Set is disabled.
     * @param disabled
     * @return
     */
    public CallCompositeCustomButtonViewData setDisabled(final boolean disabled) {
        this.disabled = disabled;
        onFieldUpdated();
        return this;
    }

    /**
     * Get button on click listener.
     * @return {@link CallCompositeEventHandler}
     */
    public CallCompositeEventHandler<Object> getOnClickEventHandler() {
        return onClickEventHandler;
    }

    /**
     * Set button on click listener.
     * @param onClickEventHandler
     * @return {@link CallCompositeEventHandler}.
     */
    public CallCompositeCustomButtonViewData setOnClickEventHandler(
            final CallCompositeEventHandler<Object> onClickEventHandler) {
        this.onClickEventHandler = onClickEventHandler;
        onFieldUpdated();
        return this;
    }


    CallCompositeEventHandler<CallCompositeCustomButtonViewData> getOnFieldUpdatedListener() {
        return onFieldUpdatedListener;
    }

    void setOnFieldUpdatedListener(
            final CallCompositeEventHandler<CallCompositeCustomButtonViewData> onFieldUpdatedListener) {
        this.onFieldUpdatedListener = onFieldUpdatedListener;
    }

    private void onFieldUpdated() {
        if (onFieldUpdatedListener != null) {
            onFieldUpdatedListener.handle(this);
        }
    }
}
