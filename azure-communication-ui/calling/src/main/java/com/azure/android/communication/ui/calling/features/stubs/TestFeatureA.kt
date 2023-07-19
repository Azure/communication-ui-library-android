package com.azure.android.communication.ui.calling.features.stubs

import com.azure.android.communication.ui.calling.features.AcsFeature

open class TestFeatureA : AcsFeature {
    open val testValue get() = "Disabled"
}