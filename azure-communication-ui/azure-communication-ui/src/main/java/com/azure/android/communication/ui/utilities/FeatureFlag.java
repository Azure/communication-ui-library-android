// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.utilities;

import android.app.Application;

public interface FeatureFlag {
    int defaultBooleanId();
    int labelId();
    boolean isActive();
    void onStart(Application application);
    void onEnd(Application application);
    void toggle();
}
