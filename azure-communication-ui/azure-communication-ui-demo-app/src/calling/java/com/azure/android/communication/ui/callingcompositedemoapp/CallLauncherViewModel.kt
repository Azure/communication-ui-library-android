// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.webkit.URLUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azure.android.communication.ui.callingcompositedemoapp.launcher.CallingCompositeJavaLauncher
import com.azure.android.communication.ui.callingcompositedemoapp.launcher.CallingCompositeKotlinLauncher
import com.azure.android.communication.ui.callingcompositedemoapp.launcher.CallingCompositeLauncher
import com.azure.android.communication.ui.callingcompositedemoapp.launcher.ChatCompositeKotlinLauncher
import java.util.concurrent.Callable

class CallLauncherViewModel : ViewModel() {
    private var token: String? = null
    private val fetchResultInternal = MutableLiveData<Result<CallingCompositeLauncher?>>()

    val fetchResult: LiveData<Result<CallingCompositeLauncher?>> = fetchResultInternal
    var isKotlinLauncher = true; private set
    var isTokenFunctionOptionSelected = false; private set

    private fun chatLauncher() = ChatCompositeKotlinLauncher()

    private fun launcher(tokenRefresher: Callable<String>) = if (isKotlinLauncher) {
        CallingCompositeKotlinLauncher(tokenRefresher)
    } else {
        CallingCompositeJavaLauncher(tokenRefresher)
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

    fun doLaunch(tokenFunctionURL: String, acsToken: String) {
        when {
            isTokenFunctionOptionSelected && urlIsValid(tokenFunctionURL) -> {
                token = null
                fetchResultInternal.postValue(
                    Result.success(launcher(UrlTokenFetcher(tokenFunctionURL)))
                )
            }
            acsToken.isNotBlank() -> {
                token = acsToken
                fetchResultInternal.postValue(
                    Result.success(launcher(CachedTokenFetcher(acsToken)))
                )
            }
            else -> {
                fetchResultInternal.postValue(
                    Result.failure(IllegalStateException("Invalid Token function URL or acs Token"))
                )
            }
        }
    }

    fun launchChat() {
        chatLauncher()
    }

    private fun urlIsValid(url: String) = url.isNotBlank() && URLUtil.isValidUrl(url.trim())
}
