// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallCompositeEventHandler;

public final class CallCompositeCustomButtonViewData {

    private String description;
    private int imageResourceId;
    private CallCompositeCustomButtonType customButtonType;
    private Integer badgeNumber;
    private Integer isDisabled;
    private CallCompositeEventHandler<Object> onClickEventHandler;
    private CallCompositeEventHandler<CallCompositeCustomButtonViewData> onFieldUpdatedListener;

    public CallCompositeCustomButtonViewData(final CallCompositeCustomButtonType customButtonType,
                             final int imageResourceId,
                             final String description,
                             final CallCompositeEventHandler<Object> onClickEventHandler) {

        this.customButtonType = customButtonType;
        this.imageResourceId = imageResourceId;
        this.description = description;
        this.onClickEventHandler = onClickEventHandler;
    }

    public String getDescription() {
        return description;
    }

    public CallCompositeCustomButtonViewData setContentDescription(final String description) {
        this.description = description;
        onFieldUpdated();
        return this;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public CallCompositeCustomButtonViewData setImageResourceId(final int imageResourceId) {
        this.imageResourceId = imageResourceId;
        onFieldUpdated();
        return this;
    }

    public CallCompositeCustomButtonType getCustomButtonType() {
        return customButtonType;
    }

    public CallCompositeCustomButtonViewData setCustomButtonType(final CallCompositeCustomButtonType customButtonType) {
        this.customButtonType = customButtonType;
        onFieldUpdated();
        return this;
    }

    public Integer getBadgeNumber() {
        return badgeNumber;
    }

    public CallCompositeCustomButtonViewData setBadgeNumber(final Integer badgeNumber) {
        this.badgeNumber = badgeNumber;
        onFieldUpdated();
        return this;
    }

    public Integer getIsDisabled() {
        return isDisabled;
    }

    public CallCompositeCustomButtonViewData setIsDisabled(final Integer isDisabled) {
        this.isDisabled = isDisabled;
        onFieldUpdated();
        return this;
    }

    public CallCompositeEventHandler<Object> getOnClickEventHandler() {
        return onClickEventHandler;
    }

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
