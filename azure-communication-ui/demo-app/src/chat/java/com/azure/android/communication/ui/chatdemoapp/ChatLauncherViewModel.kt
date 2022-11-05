// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azure.android.communication.ui.chat.ChatThreadManager
import com.azure.android.communication.ui.chatdemoapp.launcher.ChatCompositeJavaLauncher
import com.azure.android.communication.ui.chatdemoapp.launcher.ChatCompositeKotlinThreadJoiner
import com.azure.android.communication.ui.chatdemoapp.launcher.ChatCompositeLauncher
import java.util.concurrent.Callable

class ChatLauncherViewModel : ViewModel() {
    private var token: String? = null
    private val fetchResultInternal = MutableLiveData<Result<ChatCompositeLauncher?>>()

    val fetchResult: LiveData<Result<ChatCompositeLauncher?>> = fetchResultInternal
    var isKotlinLauncher = true; private set
    var isTokenFunctionOptionSelected = false; private set
    var chatThreadManager : MutableLiveData<ChatThreadManager> = MutableLiveData()
    val isConnected get() = chatThreadManager.value != null

    private fun launcher(tokenRefresher: Callable<String>) = if (isKotlinLauncher) {
        ChatCompositeKotlinThreadJoiner(tokenRefresher)
    } else {
        ChatCompositeJavaLauncher(tokenRefresher)
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

    fun doLaunch(acsToken: String) {
        when {
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
}
