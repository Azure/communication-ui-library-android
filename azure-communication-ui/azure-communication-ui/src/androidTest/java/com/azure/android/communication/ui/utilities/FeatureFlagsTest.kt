// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.utilities

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.utilities.implementation.FEATURE_FLAG_SHARED_PREFS_KEY
import com.azure.android.communication.ui.utilities.implementation.FeatureFlagEntry
import com.azure.android.communication.ui.utilities.implementation.FeatureFlags
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// Test the feature flag system
@RunWith(AndroidJUnit4::class)
class FeatureFlagsTest {
    @Before
    fun clearSharedPrefs() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        appContext.getSharedPreferences(FEATURE_FLAG_SHARED_PREFS_KEY, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }

    @Test
    fun testEnumFlag() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        FeatureFlags.initialize(appContext)
        assert(
            FeatureFlags.BluetoothAudio.active == appContext.resources.getBoolean(R.bool.azure_communication_ui_feature_flag_bluetooth_audio),
            { "Bluetooth should be enabled" }
        )
        FeatureFlags.BluetoothAudio.toggle()
        assert(!FeatureFlags.BluetoothAudio.active, { "Bluetooth should be enabled now" })
    }

    @Test
    fun testAdditionalFeature() {
        //  Fake an entry
        var started = false
        val entry = FeatureFlagEntry(
            defaultBooleanId = R.bool.azure_communication_ui_feature_flag_test_false,
            labelId = R.string.azure_communication_ui_feature_flag_test_label,
            start = {
                started = true
            },
            end = {
                started = false
            }
        )

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        FeatureFlags.registerAdditionalFeature(entry)

        FeatureFlags.initialize(appContext)
        assert(
            entry.active == appContext.resources.getBoolean(R.bool.azure_communication_ui_feature_flag_test_false),
            { "Should be disabled" }
        )
        entry.toggle()
        assert(entry.active) { "Should be enabled (active)" }
        assert(started) { "Should have been started" }
        entry.toggle()
        assert(!entry.active) { "Should be disabled" }
        assert(!started) { "Should have been stopped" }
        // / Check if the features list is 1 more than the Enum list size
        assert(FeatureFlags.features.size == FeatureFlags.values().size + 1)
    }

    @Test
    fun testAutoStartFeature() {
        // / Fake an entry (this one will be default on)
        var started = false
        val entry = FeatureFlagEntry(
            defaultBooleanId = R.bool.azure_communication_ui_feature_flag_test_true,
            labelId = R.string.azure_communication_ui_feature_flag_test_label,
            start = {
                started = true
            },
            end = {
                started = false
            }
        )

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        FeatureFlags.registerAdditionalFeature(entry)
        FeatureFlags.initialize(appContext)
        assert(started) { "Should have been started" }
    }
}
