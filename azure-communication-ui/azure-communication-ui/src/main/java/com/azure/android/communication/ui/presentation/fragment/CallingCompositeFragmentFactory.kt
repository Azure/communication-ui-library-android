// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.azure.android.communication.ui.presentation.VideoViewManager
import com.azure.android.communication.ui.presentation.fragment.calling.CallingFragment
import com.azure.android.communication.ui.presentation.fragment.setup.SetupFragment

internal class CallingCompositeFragmentFactory(
    private val viewModelFactory: ViewModelFactory,
    private val videoViewManager: VideoViewManager,
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            SetupFragment::class.java.name -> SetupFragment(
                viewModelFactory.setupViewModel,
                videoViewManager
            )
            CallingFragment::class.java.name -> CallingFragment(
                viewModelFactory.callViewModel,
                videoViewManager
            )
            else -> super.instantiate(classLoader, className)
        }
    }
}
