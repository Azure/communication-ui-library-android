// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.azure.android.communication.ui.di.DependencyInjectionContainer
import com.azure.android.communication.ui.di.DependencyInjectionContainerImpl
import java.lang.RuntimeException

internal class DependencyInjectionContainerHolder(application: Application) : AndroidViewModel(application) {

    var instanceId : Int = -1

    val container: DependencyInjectionContainer by lazy {
        if (instanceId == -1) {
            throw RuntimeException("Will not be able to locate a Configuration for instanceId: -1. " +
                    "Please ensure that you have set instanceId before retrieving the container.")
        }

        DependencyInjectionContainerImpl(application, instanceId)
    }

}
