// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azure.android.communication.ui.chatdemoapp.launcher.ChatCompositeJavaLauncher
import com.azure.android.communication.ui.chatdemoapp.launcher.ChatCompositeKotlinLauncher
import com.azure.android.communication.ui.chatdemoapp.launcher.ChatCompositeLauncher
import com.azure.android.communication.ui.demoapp.UrlTokenFetcher

class ChatLauncherViewModel : ViewModel() {
    private val fetchResultInternal = MutableLiveData<Result<ChatCompositeLauncher?>>()

    val fetchResult: LiveData<Result<ChatCompositeLauncher?>> = fetchResultInternal
    var isKotlinLauncher = true; private set

    private fun launcher() = if (isKotlinLauncher) {
        ChatCompositeKotlinLauncher(UrlTokenFetcher("todo"))
    } else {
        ChatCompositeJavaLauncher(UrlTokenFetcher("todo"))
    }

    fun destroy() {
        fetchResultInternal.value = Result.success(null)
    }

    fun setJavaLauncher() {
        isKotlinLauncher = false
    }

    fun setKotlinLauncher() {
        isKotlinLauncher = true
    }

    fun doLaunch() {
        fetchResultInternal.postValue(Result.success(launcher()))
    }
}
