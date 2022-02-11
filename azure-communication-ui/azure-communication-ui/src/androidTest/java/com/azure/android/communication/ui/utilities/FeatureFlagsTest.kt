package com.azure.android.communication.ui.utilities

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.ui.R
import org.junit.Test
import org.junit.runner.RunWith

// Test the feature flag system
@RunWith(AndroidJUnit4::class)
class FeatureFlagsTest {
    @Test
    fun testEnumFlag() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        FeatureFlags.initialize(appContext)
        assert(FeatureFlags.BluetoothAudio.active == appContext.resources.getBoolean(R.bool.azure_communication_ui_feature_flag_bluetooth_audio), {"Bluetooth should be disabled"})
        FeatureFlags.BluetoothAudio.toggle()
        assert(FeatureFlags.BluetoothAudio.active, {"Bluetooth should be enabled now"})
    }


    @Test
    fun testAdditionalFeature() {
        /// Fake an entry (it'll just duplicate Screen Share Zoom
        var started = false
        val entry = FeatureFlagEntry(
            labelId = R.string.azure_communication_ui_feature_flag_test_label,
            defaultBooleanId = R.bool.azure_communication_ui_feature_flag_test_false,
            onStart = {
                started = true
            },
            onEnd = {
                started = false
            },
        )
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        FeatureFlags.registerAdditionalFeature(entry)

        FeatureFlags.initialize(appContext)
        assert(entry.active == appContext.resources.getBoolean(R.bool.azure_communication_ui_feature_flag_test_false), {"Should be disabled"})
        entry.toggle()
        assert(entry.active) { "Should be enabled (active)" }
        assert(started) { "Should have been started" }
        entry.toggle()
        assert(!entry.active) { "Should be disabled" }
        assert(!started) { "Should have been stopped" }
    }

    @Test
    fun testAutoStartFeature() {
        /// Fake an entry (it'll just duplicate Screen Share Zoom
        var started = false
        val entry = FeatureFlagEntry(
            labelId = R.string.azure_communication_ui_feature_flag_test_label,
            defaultBooleanId = R.bool.azure_communication_ui_feature_flag_test_true,
            onStart = {
                started = true
            },
            onEnd = {
                started = false
            },
        )
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        FeatureFlags.registerAdditionalFeature(entry)
        FeatureFlags.initialize(appContext)
        assert(started) { "Should have been started" }
    }
}