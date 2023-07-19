package com.azure.android.communication.ui.calling.features.implementations

import com.azure.android.communication.ui.calling.features.stubs.TestFeatureA

class TestFeatureAImpl : TestFeatureA() {
    override val testValue get() = "Enabled"
}
