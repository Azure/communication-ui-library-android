// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.utilities.implementation

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.azure.android.communication.ui.R

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
enum class FeatureFlags(
    // Label to display on screen
    override val labelId: Int,

    override val fallbackBoolean: Boolean,
    override val fallbackLabel: String,

) : FeatureFlag {
    // ---------------------------- Global Features -------------------------------------------------
    // These features are global to the composite. They are available via the FeatureFlags enum.
    BluetoothAudio(
        R.string.azure_communication_ui_feature_flag_bluetooth_audio_label,
        true,
        "Bluetooth Audio"

    ),
    ScreenShareZoom(
        R.string.azure_communication_ui_feature_screen_share_zoom_label,
        true,
        "Screen Share Zoom"
    );
    // ---------------------------- End Global Features ---------------------------------------------

    // Stubs for onStart/onEnd as we don't need it for the enum ones.
    override val onEnd: (application: Application) -> Unit
        get() = {}

    override val onStart: (application: Application) -> Unit
        get() = {}

    // FeatureFlag Interface/Companion
    //
    // 1) `registerAdditionalEntry(FeatureFlag)` to add optional features at runtime
    // 2) `initialize(context)` at app/activity start to initialize/start the system
    // 3) `features` to get the list of all the available Feature Flags
    companion object {
        private val additionalEntries = ArrayList<FeatureFlag>()

        fun registerAdditionalFeature(feature: FeatureFlag) {
            if (!additionalEntries.contains(feature)) {
                additionalEntries.add(feature)
            }
        }

        // List of all features
        val features: List<FeatureFlag> get() = values().toList() + additionalEntries

        // The delegate to use for getting/setting, default in-memory
        var getterSetterDelegate : FeatureGetterSetter = DefaultFeatureGetterSetter()
    }
}

// Class to add additional Entries to the FeatureFlag system (e.g. from the Demo, or another app)
data class FeatureFlagEntry(
    override val labelId: Int,
    override val fallbackBoolean: Boolean,
    override val fallbackLabel: String,
    private val start: (application: Application) -> Unit,
    private val end: (application: Application) -> Unit,

) : FeatureFlag {

    override val onStart: (application: Application) -> Unit
        get() = {
            if (active) start(it)
        }

    override val onEnd: (application: Application) -> Unit
        get() = {
            if (!active) end(it)
        }
}

// A Feature Flag
// This interface is shared between the Optional and Enum features
interface FeatureFlag {
    val labelId: Int
    val onStart: (application: Application) -> Unit
    val onEnd: (application: Application) -> Unit

    val fallbackBoolean: Boolean
    val fallbackLabel: String

    val label : String
        get() = FeatureFlags.getterSetterDelegate.getLabel(key)

    val key: String
        get() = "$labelId"


    // Getters and Setters for Active
    // 1) SharedPreferences priority
    // 2) fallback to resource with defaultBooleanId
    var active: Boolean
        get() = FeatureFlags.getterSetterDelegate.get(key)
        set(value) = FeatureFlags.getterSetterDelegate.set(key, value)

    // Toggle a feature flag
    fun toggle() {
        active = !active
    }
}

interface FeatureGetterSetter {
     fun set(name: String, value: Boolean)
     fun get(name: String) : Boolean
     fun getLabel(name: String) : String
}

class DefaultFeatureGetterSetter : FeatureGetterSetter {
    val values = HashMap<String, Boolean>()
    override fun set(name: String, value: Boolean) {
        values[name] = value
    }

    override fun get(name: String): Boolean {
        return values[name] ?: FeatureFlags.features.filter { it.key == name }.first().fallbackBoolean
    }

    override fun getLabel(name: String): String {
        return FeatureFlags.features.filter { it.key == name }.first().fallbackLabel
    }
}


// Key for the SharedPrefs store that will be used for FeatureFlags
const val FEATURE_FLAG_SHARED_PREFS_KEY = "FeatureFlags"
