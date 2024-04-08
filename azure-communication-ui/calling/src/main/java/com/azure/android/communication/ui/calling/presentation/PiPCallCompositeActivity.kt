package com.azure.android.communication.ui.calling.presentation

import android.os.Bundle

internal open class MultitaskingCallCompositeActivity : CallCompositeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}

internal class PiPCallCompositeActivity : MultitaskingCallCompositeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
