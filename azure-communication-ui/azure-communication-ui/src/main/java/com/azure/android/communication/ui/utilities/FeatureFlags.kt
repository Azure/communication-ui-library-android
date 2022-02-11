package com.azure.android.communication.ui.utilities

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.azure.android.communication.ui.R

/* Feature Flag Management

There is 2 parts to this system.
1) Enum entries (fixed features). These are optional features in the Composite Library itself
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
    // Id of the bool resource containing the default
    override val defaultBooleanId: Int,

    // Label to display on screen
    override val labelId: Int,
) : FeatureFlag {
    // / Fixed Feature List
    // / To add Feature to this enum, just add new Keys
    BluetoothAudio(
        R.bool.azure_communication_ui_feature_flag_bluetooth_audio,
        R.string.azure_communication_ui_feature_flag_bluetooth_audio_label
    ),
    ScreenShareZoom(
        R.bool.azure_communication_ui_feature_screen_share_zoom,
        R.string.azure_communication_ui_feature_screen_share_zoom_label,
    );

    override val onEnd: (application: Application) -> Unit
        get() = {}

    override val onStart: (application: Application) -> Unit
        get() = {}


    companion object {
        lateinit var applicationContext: Application
        lateinit var sharedPrefs: SharedPreferences

        // Ensure this has an ApplicationContext to get SharedPreferences from
        // This will allow us to access the current value and also the Resources to read the default
        fun initialize(context: Context) {
            applicationContext = context.applicationContext as Application
            sharedPrefs = applicationContext.getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE)

            /// Start default features
            features.filter { it.active }.forEach { it.onStart(applicationContext) }
        }

        private val additionalEntries = ArrayList<FeatureFlagEntry>()

        fun registerAdditionalFeature(feature: FeatureFlagEntry) {
            if (!additionalEntries.contains(feature)) {
                additionalEntries.add(feature)
            }
        }

        // / List of all features
        val features: List<FeatureFlag> get() = values().toList() + additionalEntries
    }
}

// Class to add additional Entries to the FeatureFlag system (e.g. from the Demo, or another app)
data class FeatureFlagEntry(
    override val defaultBooleanId: Int,
    override val labelId: Int,
    override val onStart: (application: Application) -> Unit,
    override val onEnd: (application: Application) -> Unit,
) : FeatureFlag

interface FeatureFlag {
    val defaultBooleanId: Int
    val labelId: Int
    val onStart: (application: Application) -> Unit
    val onEnd: (application: Application) -> Unit

    val key: String
        get() = "$defaultBooleanId"

    var active: Boolean
        get() = FeatureFlags.sharedPrefs.getBoolean(key, FeatureFlags.applicationContext.resources.getBoolean(defaultBooleanId))
        set(value) {
            if (value != active) {
                // Toggled
                if (value) {
                    onStart(FeatureFlags.applicationContext)
                } else {
                    onEnd(FeatureFlags.applicationContext)
                }
            }
            FeatureFlags.sharedPrefs.edit().putBoolean(key, value).apply()
        }

    val label: String
        get() = FeatureFlags.applicationContext.getString(labelId)

    fun toggle() { active = !active }
}

// Key for the SharedPrefs store that will be used for FeatureFlags
const val FEATURE_FLAG_SHARED_PREFS_KEY = "FeatureFlags"
