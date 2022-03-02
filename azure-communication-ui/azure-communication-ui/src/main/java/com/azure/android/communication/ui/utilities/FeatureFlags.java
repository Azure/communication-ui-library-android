// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.utilities;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.azure.android.communication.ui.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/* Feature Flag Management

There is 2 parts to this system.
1) Enum entries (fixed features). These are features in the Composite Library itself
2) Pluggable entries (dynamic features). This allows the Application (e.g. Demo, Contoso) to add
   additional features using the system.

Initialization:
  The feature flag system requires an application context to access String and Boolean resources
  as well as the SharedPreferences to retain choices

  `FeatureFlags.initialize(context)` anywhere early in your code e.g. (activity/application).onCreate

# Usage:
  ## Checking Flags:
  ### Built-in
  `FeatureFlags.FlagName.active = true/false`
  (enable/disable) a feature or check it's status

  ### Add-on
  `yourFeatureFlag.active = true/false`
  (enable/disable) a feature or check it's status

  ## Registering a feature from outside UI Composite Library
  `FeatureFlags.registerAdditionalFeature(yourFeatureFlag)`

  ## Reading all FeatureFlags (Enum + Addon)
  `FeatureFlags.features`
  This is recommended over "values()" which will only access the Enum values.

 */

public enum FeatureFlags implements FeatureFlag {

    // ---------------------------- Global Features -------------------------------------------------
    // These features are global to the composite. They are available via the FeatureFlags enum.
    BluetoothAudio(
            R.bool.azure_communication_ui_feature_flag_bluetooth_audio,
            R.string.azure_communication_ui_feature_flag_bluetooth_audio_label
    ),
    ScreenShareZoom(
            R.bool.azure_communication_ui_feature_screen_share_zoom,
            R.string.azure_communication_ui_feature_screen_share_zoom_label
            );

    FeatureFlags(int defaultBooleanId, int labelId) {
        this.defaultBooleanId = defaultBooleanId;
        this.labelId = labelId;
        this.onStart = (application)->{};
        this.onEnd = (application)->{};
    }

    // Id of the bool resource containing the default
    int defaultBooleanId;

    // Label to display on screen
    int labelId;

    Consumer<Application> onStart;
    Consumer<Application> onEnd;

    // ---------------------------- End Global Features ---------------------------------------------

    // FeatureFlag Interface/Companion
    //
    // 1) `registerAdditionalEntry(FeatureFlag)` to add optional features at runtime
    // 2) `initialize(context)` at app/activity start to initialize/start the system
    // 3) `features` to get the list of all the available Feature Flags

        private static Application applicationContext;
        private static SharedPreferences sharedPrefs;

        // Key for the SharedPrefs store that will be used for FeatureFlags
        public static final String FEATURE_FLAG_SHARED_PREFS_KEY = "FeatureFlags";

        // Ensure this has an ApplicationContext to get SharedPreferences from
        // This will allow us to access the current value and also the Resources to read the default
        public static void initialize(Context context) {

            applicationContext = (Application) context.getApplicationContext();
            sharedPrefs = applicationContext.getSharedPreferences(
                FEATURE_FLAG_SHARED_PREFS_KEY,
                Context.MODE_PRIVATE
            );

            // Start default features
            for (FeatureFlag feature:features()) {
                if (FeatureFlags.isActive(feature)) {
                    feature.onStart(applicationContext);
                }
            }
        }

        private static final List<FeatureFlag> additionalEntries = new ArrayList<>();

        public static boolean isActive(FeatureFlag flag) {
            // If not added to the system, return false
            if (!FeatureFlags.features().contains(flag)) {
                return false;
            }

            String key = ""+flag.defaultBooleanId();
            return FeatureFlags.sharedPrefs.getBoolean(
                    key,
                    FeatureFlags.applicationContext.getResources().getBoolean(flag.defaultBooleanId())
            );
        }

        public static void setActive(FeatureFlag flag, boolean value) {
            final boolean wasActive = isActive(flag);
            FeatureFlags.sharedPrefs.edit().putBoolean(""+flag.defaultBooleanId(), value).apply();
            if (value != wasActive) {
                // Toggled
                if (value) {
                    flag.onStart(FeatureFlags.applicationContext);
                } else {
                    flag.onEnd(FeatureFlags.applicationContext);
                }
            }
        }

        public static void registerAdditionalFeature(FeatureFlag feature) {
            if (!additionalEntries.contains(feature)) {
                additionalEntries.add(feature);
            }
        }

        public static List<FeatureFlag> features() {
            final List<FeatureFlag> list = new ArrayList<>(additionalEntries);
            Collections.addAll(list, values());
            return list;
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
    public void onStart(Application application) {}

    @Override
    public void onEnd(Application application) {}

    @Override
    public boolean isActive() {
        return FeatureFlags.isActive(this);
    }

    @Override
    public void toggle() {
        setActive(this, !isActive());
    }
}


interface FeatureFlagAppHook {
    void call(Application application);
}
// A Feature Flag
// This interface is shared between the Optional and Enum features


