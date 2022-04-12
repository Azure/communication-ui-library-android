// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.utilities.implementation

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
    override val onEnd: () -> Unit
        get() = {}

    override val onStart: () -> Unit
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
        var getterSetterDelegate : FeatureGetterSetter = FallbackFeatureGetterSetter()
    }
}

// Class to add additional Entries to the FeatureFlag system (e.g. from the Demo, or another app)
data class FeatureFlagEntry(
    override val labelId: Int,
    override val fallbackBoolean: Boolean,
    override val fallbackLabel: String,
    private val start: () -> Unit,
    private val end: () -> Unit,

) : FeatureFlag {

    override val onStart: () -> Unit
        get() = {
            if (active) start()
        }

    override val onEnd: () -> Unit
        get() = {
            if (!active) end()
        }
}

// A Feature Flag
// This interface is shared between the Optional and Enum features
interface FeatureFlag {
    val labelId: Int
    val onStart: () -> Unit
    val onEnd: () -> Unit

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

// Delegate interface for getting/setting the feature flag values
//
// Default implementation won't use context but will persist in memory changes,
// while other implementations could either fix the values (i.e. no setter), or used SharedPrefs.

abstract class FeatureGetterSetter {
    private val listeners = ArrayList<Runnable>()

    fun addListener(callback: Runnable) {
        listeners.add(callback)
    }

    fun removeListener(callback: Runnable) {
        listeners.remove(callback)
    }

    // Call this to notify the listeners in the setter
    fun notifyListeners() {
        for (listener in listeners) {
            listener.run()
        }
    }

    abstract fun set(key: String, value: Boolean)
    abstract fun get(key: String) : Boolean
    abstract fun getLabel(key: String) : String
}

// FallbackFeatureGetterSetter
//
// Default implementation, persists to memory, doesn't use the labelID (no context)
class FallbackFeatureGetterSetter : FeatureGetterSetter() {
    val values = HashMap<String, Boolean>()
    override fun set(key: String, value: Boolean) {

        if (FeatureFlags.features.none { it.key == key })
            return

        values[key] = value
        notifyListeners()
    }

    override fun get(key: String): Boolean {
        if (FeatureFlags.features.none { it.key == key })
            return false

        return values[key] ?: FeatureFlags.features.first { it.key == key }.fallbackBoolean
    }

    override fun getLabel(key: String): String {
        return FeatureFlags.features.first { it.key == key }.fallbackLabel
    }
}