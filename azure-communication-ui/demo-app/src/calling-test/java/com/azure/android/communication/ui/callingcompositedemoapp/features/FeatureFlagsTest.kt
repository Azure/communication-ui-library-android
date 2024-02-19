// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.features

import org.junit.Before
import org.junit.Test

class FeatureFlagsTest {
    @Before
    fun clearStorage() {
        FeatureFlags.flagStoreDelegate = DefaultFeatureFlagStore()
    }

    @Test
    fun testEnumFlag() {
        FeatureFlags.NOOP.toggle()
        assert(!FeatureFlags.NOOP.active) { "Noop should be disabled now" }
    }

    @Test
    fun testAdditionalFeature() {
        //  Fake an entry
        var started = false
        val oldLength = FeatureFlags.features.size
        val entry =
            FeatureFlagEntry(
                start = {
                    started = true
                },
                end = {
                    started = false
                },
                label = "test",
                enabledByDefault = false,
            )

        FeatureFlags.registerAdditionalFeature(entry)

        assert(
            !entry.active,
            { "Should be disabled" },
        )
        entry.toggle()
        assert(entry.active) { "Should be enabled (active)" }
        assert(started) { "Should have been started" }
        entry.toggle()
        assert(!entry.active) { "Should be disabled" }
        assert(!started) { "Should have been stopped" }
        // / Check if the features list is 1 more than the Enum list size
        assert(oldLength + 1 == FeatureFlags.features.size)
    }

    @Test
    fun testAutoStartFeature() {
        // / Fake an entry (this one will be default on)
        var started = false
        val entry =
            FeatureFlagEntry(
                start = {
                    started = true
                },
                end = {
                    started = false
                },
                label = "test",
                enabledByDefault = true,
            )

        FeatureFlags.registerAdditionalFeature(entry)
        assert(started) { "Should have been started" }
    }
}
