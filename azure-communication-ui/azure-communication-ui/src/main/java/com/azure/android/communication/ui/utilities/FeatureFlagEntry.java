// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.utilities;

import android.app.Application;

import java.util.Objects;

// Class to add additional Entries to the FeatureFlag system (e.g. from the Demo, or another app)
public class FeatureFlagEntry implements FeatureFlag {
    final int defaultBooleanId;
    final int labelId;
    final FeatureFlags.FeatureFlagAppHook start;
    final FeatureFlags.FeatureFlagAppHook end;

    public FeatureFlagEntry(final int defaultBooleanId, final int labelId, final FeatureFlags.FeatureFlagAppHook start, final FeatureFlags.FeatureFlagAppHook end) {
        this.defaultBooleanId = defaultBooleanId;
        this.labelId = labelId;
        this.start = start;
        this.end = end;
    }

    @Override
    public int defaultBooleanId() {
        return defaultBooleanId;
    }

    @Override
    public int labelId() {
        return labelId;
    }

    @Override
    public void onStart(final Application application) {
        if (start != null) { start.call(application); }
    }

    @Override
    public void onEnd(final Application application) {
        if (end != null) { end.call(application); }
    }

    @Override
    public boolean isActive() {
        return FeatureFlags.isActive(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureFlagEntry that = (FeatureFlagEntry) o;
        return defaultBooleanId == that.defaultBooleanId && labelId == that.labelId;
    }

    @Override
    public void toggle() {
        FeatureFlags.setActive(this, !isActive());
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultBooleanId, labelId);
    }
}
