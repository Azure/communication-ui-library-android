package com.azure.android.communication.ui.calling.features.implementations

import com.azure.android.communication.ui.calling.features.FeatureInterfaces

class TestFeatureAImpl : FeatureInterfaces.TestFeatureA() {
    override val value: String
        get() = "Feature A"
}