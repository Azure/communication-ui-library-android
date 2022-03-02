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
