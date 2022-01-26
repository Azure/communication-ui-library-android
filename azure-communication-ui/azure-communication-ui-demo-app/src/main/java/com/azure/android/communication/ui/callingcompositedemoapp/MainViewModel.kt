package com.azure.android.communication.ui.callingcompositedemoapp

import android.webkit.URLUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azure.android.communication.ui.callingcompositedemoapp.launcher.CallingCompositeJavaLauncher
import com.azure.android.communication.ui.callingcompositedemoapp.launcher.CallingCompositeKotlinLauncher
import com.azure.android.communication.ui.callingcompositedemoapp.launcher.CallingCompositeLauncher
import com.github.kittinunf.fuel.httpGet
import org.json.JSONObject
import java.io.IOException

class MainViewModel : ViewModel() {
    private var token: String? = null

    var isKotlinLauncher = true; private set
    var isTokenFunctionOptionSelected = false; private set

    private fun launcher(tokenUrl: String) =
        if (isKotlinLauncher) CallingCompositeKotlinLauncher(UrlTokenFetcher(tokenUrl))
        else CallingCompositeJavaLauncher(UrlTokenFetcher(tokenUrl))

    private val fetchResultInternal = MutableLiveData<Result<CallingCompositeLauncher?>>()
    val fetchResult: LiveData<Result<CallingCompositeLauncher?>> = fetchResultInternal

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
            isTokenFunctionOptionSelected -> {
                token = null
                fetchToken(tokenFunctionURL)
            }
            acsToken.isNotBlank() -> {
                token = acsToken
                fetchResultInternal.value = Result.success(launcher(tokenFunctionURL))
            }
            else -> fetchResultInternal.value = Result.failure(
                IllegalStateException("Invalid Token function or acs Token")
            )
        }
    }

    private fun urlIsValid(url: String) = url.isNotBlank() && URLUtil.isValidUrl(url.trim())

    // / Need to evaluate this and the Token Function a bit more
    // / Do we need as part of ViewModel? How to Test?
    private fun fetchToken(tokenFunctionURL: String) {

        if (urlIsValid(tokenFunctionURL)) {
            tokenFunctionURL
                .httpGet()
                .responseString { result ->
                    val response = result.component1()
                    val cause = result.component2()
                    if (cause != null || response == null) {
                        fetchResultInternal.postValue(Result.failure(IOException("Unable to fetch token: ", cause)))
                    } else {
                        token = JSONObject(response).getString("token")
                        fetchResultInternal.postValue(Result.success(launcher(tokenFunctionURL)))
                    }
                }
        } else {
            fetchResultInternal.value = Result.failure(IllegalStateException("Token function URL error"))
        }
    }

    private fun getToken() = token!!
}
