package com.azure.android.communication.ui.utilities

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.azure.android.communication.ui.R

// Enum of all the current feature flags
enum class FeatureFlags(
    // Id of the bool resource containing the default
    private val bool_id : Int,

    // Label to display on screen
    val label : String,
) {
    /// To add Feature to this enum, just add new Keys
    BluetoothAudio(R.bool.azure_communication_ui_feature_flag_bluetooth_audio, "Bluetooth"),
    ScreenShareZoom(R.bool.azure_communication_ui_feature_screen_share_zoom, "ScreenShareZoom");

    // Check or Set if this flag is currently active
    var active : Boolean
        get() = sharedPrefs.getBoolean("$bool_id", applicationContext.resources.getBoolean(bool_id))
        set(value) {
            sharedPrefs.edit().putBoolean("$bool_id", value).apply()
        }


    fun toggle() { active = !active }

    companion object {
        lateinit var applicationContext : Application
        lateinit var sharedPrefs : SharedPreferences

        // Ensure this has an ApplicationContext to get SharedPreferences from
        // This will allow us to access the current value and also the Resources to read the default
        fun initialize(context: Context) {
            this.applicationContext = context.applicationContext as Application
            sharedPrefs = applicationContext.getSharedPreferences("FeatureFlags", Context.MODE_PRIVATE)
        }

    }
}

// Class to add additional Entries to the FeatureFlag system (e.g. from the Demo, or another app)
data class FeatureFlagEntry(
    val label: String,
    val onStart: (application:Application) -> Unit,
    val onEnd: (application:Application) -> Unit,
);