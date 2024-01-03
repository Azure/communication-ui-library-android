// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.views

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import com.azure.android.communication.ui.callingcompositedemoapp.CallLauncherApplication
import com.azure.android.communication.ui.callingcompositedemoapp.R

internal class EndCompositeButtonView private constructor(
    private val context: Context,
) {
    companion object {
        var buttonView: EndCompositeButtonView? = null
        private var isInitialized = false
        const val DEFAULT_GRAVITY = Gravity.TOP or Gravity.START
        const val POSITION_X = 500
        const val POSITION_L = 200

        fun get(context: Context): EndCompositeButtonView {
            if (buttonView == null) {
                buttonView = EndCompositeButtonView(context)
            }
            return buttonView!!
        }
    }

    private val endCallButton: Button =
        LayoutInflater.from(context).inflate(R.layout.end_composite_button, null) as Button
    private val windowManager: WindowManager =
        endCallButton.context.getSystemService(Service.WINDOW_SERVICE) as WindowManager

    fun show() {
        if (drawOverlaysPermission(context) && endCallButton.visibility != View.VISIBLE) {
            if (!isInitialized) {
                isInitialized = true
                init()
            }
            endCallButton.visibility = View.VISIBLE
            endCallButton.setOnClickListener {
                (context.applicationContext as CallLauncherApplication).callCompositeManager?.callHangup()
            }
        }
    }

    fun hide() {
        if (endCallButton.visibility == View.VISIBLE) {
            endCallButton.visibility = View.GONE
        }
    }

    fun updateText(text: String) {
        endCallButton.text = context.getText(R.string.exit_composite_button_text).toString() + "\n" + text
    }

    private fun init() {
        val params = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888
        )
        params.gravity = DEFAULT_GRAVITY
        params.x = POSITION_X
        params.y = POSITION_L
        windowManager.addView(endCallButton, params)
        endCallButton.setOnTouchListener(MovingTouchListener(params, windowManager))
        endCallButton.isHapticFeedbackEnabled = false
        endCallButton.visibility = View.GONE
    }

    private fun drawOverlaysPermission(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(context)
            .also {
                if (!it) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.packageName)
                    )
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
            }
    }

    private class MovingTouchListener(
        private val params: WindowManager.LayoutParams,
        private val windowManager: WindowManager,
    ) : View.OnTouchListener {
        private var initialX = 0
        private var initialY = 0
        private var initialTouchX = 0f
        private var initialTouchY = 0f
        override fun onTouch(
            v: View,
            event: MotionEvent,
        ): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(v, params)
                }
            }
            return false
        }
    }
}
