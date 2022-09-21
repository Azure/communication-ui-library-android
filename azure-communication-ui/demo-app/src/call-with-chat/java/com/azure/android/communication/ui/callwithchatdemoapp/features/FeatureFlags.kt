// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchatdemoapp.features

/* Feature Flag Management

There is 2 parts to this system.
1) Enum entries (fixed features). These are features in the Composite Library itself
2) Pluggable entries (dynamic features). This allows the Application (e.g. Demo, Contoso) to add
   additional features using the system.

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
    override val enabledByDefault: Boolean,
    override val label: String,

) : FeatureFlag {
    // ---------------------------- Global Features -------------------------------------------------
    // These features are global to the composite. They are available via the FeatureFlags enum.

    // Empty Entry, will be ignored from values()
    // Added since all other entries are removed at the moment.
    NOOP(
        false,
        ""
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
                if (feature.active) {
                    feature.onStart()
                }
            }
        }

        // List of all features
        val features: List<FeatureFlag>
            get() = values().filter { it != NOOP }.toList() + additionalEntries

        // The delegate to use for getting/setting, default in-memory
        var flagStoreDelegate: FeatureFlagStore = DefaultFeatureFlagStore()
    }
}

// Class to add additional Entries to the FeatureFlag system (e.g. from the Demo, or another app)
data class FeatureFlagEntry(
    override val enabledByDefault: Boolean,
    override val label: String,
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
    val onStart: () -> Unit
    val onEnd: () -> Unit

    val enabledByDefault: Boolean
    val label: String

    val key: String
        get() = this.label

    // Getters and Setters for Active
    // 1) SharedPreferences priority
    // 2) fallback to resource with defaultBooleanId
    var active: Boolean
        get() = FeatureFlags.flagStoreDelegate.get(this)
        set(value) = FeatureFlags.flagStoreDelegate.set(this, value)

    // Toggle a feature flag
    fun toggle() {
        active = !active
    }
}

// Storage for FeatureFlagValues.
abstract class FeatureFlagStore {
    private val listeners = ArrayList<Runnable>()

    // Implement these to save/retrieve from your data-store
    abstract fun setInternal(flag: FeatureFlag, value: Boolean)
    abstract fun getInternal(flag: FeatureFlag): Boolean

    fun get(flag: FeatureFlag) = getInternal(flag)

    fun set(flag: FeatureFlag, enabled: Boolean) {
        val wasEnabled = get(flag)

        setInternal(flag, enabled)

        if (enabled && !wasEnabled) flag.onStart()
        if (!enabled && wasEnabled) flag.onEnd()

        notifyListeners()
    }

    fun addListener(callback: Runnable) {
        listeners.add(callback)
    }

    fun removeListener(callback: Runnable) {
        listeners.remove(callback)
    }

    private fun notifyListeners() {
        for (listener in listeners) {
            listener.run()
        }
    }
}

// FallbackFeatureGetterSetter
//
// Default implementation, persists to memory, doesn't use the labelID (no context)
class DefaultFeatureFlagStore : FeatureFlagStore() {
    val values = HashMap<FeatureFlag, Boolean>()

    override fun setInternal(flag: FeatureFlag, value: Boolean) {
        if (FeatureFlags.features.none { it == flag })
            return

        values[flag] = value
    }

    override fun getInternal(flag: FeatureFlag): Boolean {
        if (FeatureFlags.features.none { it == flag })
            return false

        return values[flag] ?: FeatureFlags.features.first { it == flag }.enabledByDefault
    }
}
