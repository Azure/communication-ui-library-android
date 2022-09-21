// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callwithchatdemoapp

import android.content.Context
import android.webkit.URLUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azure.android.communication.ui.callwithchatdemoapp.launcher.CallWithChatCompositeJavaLauncher
import com.azure.android.communication.ui.callwithchatdemoapp.launcher.CallWithChatCompositeKotlinLauncher
import com.azure.android.communication.ui.demoapp.AuthService
import com.azure.android.communication.ui.demoapp.AuthServiceLocal
import com.azure.android.communication.ui.demoapp.AuthServiceRemote
import kotlinx.coroutines.launch
import java.util.UUID

class CallLauncherViewModel : ViewModel() {
    private val fetchResultInternal = MutableLiveData<Result<Any?>>()

    val fetchResult: LiveData<Result<Any?>> = fetchResultInternal
    var isKotlinLauncher = true; private set
    var isTokenFunctionOptionSelected = false; private set

    private fun getLauncher() = if (isKotlinLauncher) {
        CallWithChatCompositeKotlinLauncher()
    } else {
        CallWithChatCompositeJavaLauncher()
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

    fun doLaunch(
        context: Context,
        alertHandler: AlertHandler,
        authApiUrl: String?,
        acsToken: String?,
        communicationUserId: String?,
        userName: String?,
        acsEndpoint: String,
        groupId: UUID?,
        chatThreadId: String?,
        meetingLink: String?,

    ) {
        val authService: AuthService = when {
            isTokenFunctionOptionSelected && authApiUrl != null && urlIsValid(authApiUrl) ->
                AuthServiceRemote(authApiUrl)
            acsToken != null && acsToken.isNotBlank() && communicationUserId != null ->
                AuthServiceLocal(acsToken, communicationUserId)
            else -> {
                fetchResultInternal.postValue(
                    Result.failure(
                        IllegalStateException(
                            "Invalid Token function URL or acs Token or CommunicationUserId"
                        )
                    )
                )
                return@doLaunch
            }
        }

        viewModelScope.launch {
            authService.ensureAuthInfo()

            fetchResultInternal.postValue(Result.success(null))

            val launcher = getLauncher()
            launcher.launch(
                context,
                alertHandler,
                authService,
                userName,
                acsEndpoint,
                groupId,
                chatThreadId,
                meetingLink,
            )
        }
    }

    private fun urlIsValid(url: String) = url.isNotBlank() && URLUtil.isValidUrl(url.trim())
}
