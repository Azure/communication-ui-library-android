package com.azure.android.communication.ui.utilities

import com.azure.android.communication.ui.utilities.implementation.DefaultFeatureFlagStore
import com.azure.android.communication.ui.utilities.implementation.FeatureFlagEntry
import com.azure.android.communication.ui.utilities.implementation.FeatureFlags
import org.junit.Before
import org.junit.Test

class FeatureFlagsTest {
    @Before
    fun clearStorage() {
        FeatureFlags.flagStoreDelegate = DefaultFeatureFlagStore()
    }

    @Test
    fun testEnumFlag() {
        FeatureFlags.BluetoothAudio.toggle()
        assert(!FeatureFlags.BluetoothAudio.active) { "Bluetooth should be disabled now" }
    }

    @Test
    fun testAdditionalFeature() {
        //  Fake an entry
        var started = false
        val entry = FeatureFlagEntry(
            start = {
                started = true
            },
            end = {
                started = false
            },
            label = "test",
            enabledByDefault = false
        )

        FeatureFlags.registerAdditionalFeature(entry)

        assert(
            !entry.active,
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
            start = {
                started = true
            },
            end = {
                started = false
            },
            label = "test",
            enabledByDefault = false
        )

        FeatureFlags.registerAdditionalFeature(entry)
        assert(started) { "Should have been started" }
    }
}