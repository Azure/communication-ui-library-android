// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp

import android.webkit.URLUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azure.android.communication.ui.chatdemoapp.launcher.ChatCompositeJavaLauncher
import com.azure.android.communication.ui.chatdemoapp.launcher.ChatCompositeKotlinLauncher
import com.azure.android.communication.ui.chatdemoapp.launcher.ChatCompositeLauncher

class CallLauncherViewModel : ViewModel() {
    private var token: String? = null
    private val fetchResultInternal = MutableLiveData<Result<ChatCompositeLauncher?>>()

    val fetchResult: LiveData<Result<ChatCompositeLauncher?>> = fetchResultInternal
    var isKotlinLauncher = true; private set
    var isTokenFunctionOptionSelected = false; private set

    private fun launcher() = if (isKotlinLauncher) {
        ChatCompositeKotlinLauncher()
    } else {
        ChatCompositeJavaLauncher()
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

    fun useTokenFunction() {
        isTokenFunctionOptionSelected = true
    }

    fun useAcsToken() {
        isTokenFunctionOptionSelected = false
    }

    fun doLaunch() {
        fetchResultInternal.postValue(Result.success(launcher()))
    }

    private fun urlIsValid(url: String) = url.isNotBlank() && URLUtil.isValidUrl(url.trim())
}
