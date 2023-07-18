package com.azure.android.communication.ui.calling.features

// Install your stubs here, i.e. define the interfaces for your features and provide default behaviors
sealed class FeatureInterfaces {
    open class TestFeatureA : AcsFeature() {
        open val value get() = "Empty"
    }

    open class TestFeatureB : AcsFeature() {
        open val value get() = "Empty"
    }
}