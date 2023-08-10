// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chatdemoapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.azure.android.communication.ui.callingcompositedemoapp.BuildConfig
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.databinding.ActivityChatLauncherBinding
import com.azure.android.communication.ui.chat.ChatAdapter
import com.azure.android.communication.ui.chat.ChatCompositeEventHandler
import com.azure.android.communication.ui.chat.models.ChatCompositeErrorEvent
import com.azure.android.communication.ui.chat.presentation.ChatThreadView
import com.azure.android.communication.ui.chatdemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.chatdemoapp.features.FeatureFlags
import com.azure.android.communication.ui.chatdemoapp.features.conditionallyRegisterDiagnostics
import com.azure.android.communication.ui.chatdemoapp.launcher.TeamsUrlParser
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import java.lang.ref.WeakReference

class ChatLauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatLauncherBinding

    private val chatLauncherViewModel: ChatLauncherViewModel by viewModels()

    private var chatView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (shouldFinish()) {
            finish()
            return
        }
        if (!AppCenter.isConfigured() && !BuildConfig.DEBUG) {
            AppCenter.start(
                application,
                BuildConfig.APP_SECRET,
                Analytics::class.java,
                Crashes::class.java,
                Distribute::class.java
            )
        }
        // Register Memory Viewer with FeatureFlags
        conditionallyRegisterDiagnostics(this)
        FeatureFlags.registerAdditionalFeature(AdditionalFeatures.secondaryThemeFeature)

        binding = ActivityChatLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data: Uri? = intent?.data
        val endpointUrl = data?.getQueryParameter("endpointurl") ?: BuildConfig.END_POINT_URL
        val threadId = data?.getQueryParameter("threadid") ?: BuildConfig.THREAD_ID
        val acsToken = data?.getQueryParameter("acstoken") ?: BuildConfig.ACS_TOKEN
        val userid = data?.getQueryParameter("userid") ?: BuildConfig.IDENTITY
        val name = data?.getQueryParameter("name") ?: BuildConfig.USER_NAME

        binding.run {
            endPointURL.setText(endpointUrl)
            acsTokenText.setText(acsToken)
            userNameText.setText(name)
            chatThreadID.setText(threadId)
            identity.setText(userid)

            launchButton.setOnClickListener {
                launch()
                launchButton.requestFocus()
                hideKeyboard()
            }

            openChatUIButton.setOnClickListener {
                showChatUI()
                hideKeyboard()
            }

            openFullScreenChatUIButton.setOnClickListener {
                showChatUIActivity()
                hideKeyboard()
            }

            stopChatCompositeButton.setOnClickListener {
                stopChatComposite()
            }

            acsTokenText.requestFocus()
            acsTokenText.isEnabled = true

            if (BuildConfig.DEBUG) {
                versionText.text = "${BuildConfig.VERSION_NAME}"
            } else {
                versionText.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            }
        }

        this.onBackPressedDispatcher.addCallback {
            if (chatView != null) {
                onChatCompositeExitRequested()
            } else {
                this.handleOnBackPressed()
            }
        }
    }

    // / When a request is made to close the view, lets do that here
    private fun onChatCompositeExitRequested() {
        // Remove chat view from screen
        binding.setupScreen.visibility = View.VISIBLE
        chatView?.parent?.let {
            (it as ViewGroup).removeView(chatView)
        }
        chatView = null
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    // check whether new Activity instance was brought to top of stack,
    // so that finishing this will get us to the last viewed screen
    private fun shouldFinish() = BuildConfig.CHECK_TASK_ROOT && !isTaskRoot

    fun showAlert(message: String) {
        runOnUiThread {
            val builder = AlertDialog.Builder(this).apply {
                setMessage(message)
                setTitle("Alert")
                setPositiveButton("OK") { _, _ ->
                }
            }
            builder.show()
        }
    }

    private fun showChatUI() {
        val chatAdapter = chatLauncherViewModel.chatAdapter!!

        // Create Chat Composite View
        chatView = ChatThreadView(this, chatAdapter)

        binding.setupScreen.visibility = View.GONE
        addContentView(
            chatView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun showChatUIActivity() {
        val chatAdapter = chatLauncherViewModel.chatAdapter!!

        val activityLauncherClass =
            Class.forName("com.azure.android.communication.ui.chat.presentation.ChatCompositeActivity")
        val constructor = activityLauncherClass.getDeclaredConstructor(Context::class.java)
        constructor.isAccessible = true
        val instance = constructor.newInstance(this)
        val launchMethod =
            activityLauncherClass.getDeclaredMethod("launch", ChatAdapter::class.java)
        launchMethod.isAccessible = true
        launchMethod.invoke(instance, chatAdapter)
    }

    private fun launch() {
        val inputChatJoinId = binding.chatThreadID.text.toString()
        val threadId = if (URLUtil.isValidUrl(inputChatJoinId))
            TeamsUrlParser.getThreadId(inputChatJoinId)
        else inputChatJoinId

        val endpoint = binding.endPointURL.text.toString()
        val acsIdentity = binding.identity.text.toString()
        val userName = binding.userNameText.text.toString()
        val acsToken = binding.acsTokenText.text.toString()

        try {
            chatLauncherViewModel.launch(
                context = this,
                errorHandler = ErrorHandler(this),
                endpoint,
                acsIdentity,
                threadId,
                userName,
                acsToken
            )
        } catch (ex: Exception) {
            if (ex.message != null) {
                val causeMessage = ex.cause?.message ?: ""
                showAlert(ex.toString() + causeMessage)
                binding.launchButton.isEnabled = true
            } else {
                showAlert("Unknown error")
            }
            return
        }

        binding.run {
            launchButton.isEnabled = true
            launchButton.visibility = View.GONE
            openChatUIButton.visibility = View.VISIBLE
            openFullScreenChatUIButton.visibility = View.VISIBLE
            stopChatCompositeButton.visibility = View.VISIBLE
        }
    }

    private fun stopChatComposite() {
        chatView = null
        chatLauncherViewModel.closeChatComposite()
        binding.run {
            launchButton.visibility = View.VISIBLE
            openChatUIButton.visibility = View.GONE
            openFullScreenChatUIButton.visibility = View.GONE
            stopChatCompositeButton.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // hide settings for chat
        // menuInflater.inflate(R.menu.launcher_activity_action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.azure_composite_show_settings -> {
            val settingIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingIntent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        val window: Window = this@ChatLauncherActivity.window
        window.navigationBarColor = ContextCompat.getColor(this@ChatLauncherActivity, R.color.white)
    }
}

// Encapsulate the Error Handler with the Activity behind a WeakReference
// To prevent leaks if the Activity is destroyed.
class ErrorHandler(chatLauncherActivity: ChatLauncherActivity) :
    ChatCompositeEventHandler<ChatCompositeErrorEvent> {
    private val wrActivity = WeakReference(chatLauncherActivity)
    override fun handle(eventArgs: ChatCompositeErrorEvent) {
        Log.e(
            "ChatCompositeDemoApp",
            "================= application is logging error ====================="
        )
        Log.e("ChatCompositeDemoApp", "${eventArgs.errorCode}", eventArgs.cause)
        Log.e(
            "ChatCompositeDemoApp",
            "===================================================================="
        )
        wrActivity.get()?.apply {
            showAlert("${eventArgs.errorCode} : ${eventArgs.cause}")
        }
    }
}
