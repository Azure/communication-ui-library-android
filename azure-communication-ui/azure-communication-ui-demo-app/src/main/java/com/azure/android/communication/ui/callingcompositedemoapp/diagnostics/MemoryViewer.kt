// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.diagnostics

import android.app.Application
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.utilities.FeatureFlagEntry
import com.azure.android.communication.ui.utilities.FeatureFlags
import com.microsoft.office.outlook.magnifierlib.Magnifier

class MemoryViewer private constructor(
    private val context: Application,
) {
    companion object {
        private var memoryViewer: MemoryViewer? = null
        private var isInitialized = false
        const val DEFAULT_GRAVITY = Gravity.TOP or Gravity.START
        const val POSITION_X = 500
        const val POSITION_L = 200

        fun getMemoryViewer(context: Application): MemoryViewer {
            if (memoryViewer == null) {
                memoryViewer = MemoryViewer(context)
            }
            return memoryViewer!!
        }
    }

    private val textView: TextView =
        LayoutInflater.from(context).inflate(R.layout.memory_view, null) as TextView
    private val windowManager: WindowManager =
        textView.context.getSystemService(Service.WINDOW_SERVICE) as WindowManager

    fun display(frameCount: Int) {
        textView.post {
            textView.text = "$frameCount mb"
        }
    }

    fun show() {
        if (drawOverlaysPermission(context) && textView.visibility != View.VISIBLE) {
            if (!isInitialized) {
                isInitialized = true
                init()
            }
            displayMemoryDiagnostics()
            textView.visibility = View.VISIBLE
        }
    }

    fun hide() {
        if (textView.visibility == View.VISIBLE) {
            textView.visibility = View.GONE
            Magnifier.stopMonitorMemory()
        }
    }

    private fun init() {
        val minWidth: Int =
            (textView.lineHeight + textView.totalPaddingTop + textView.totalPaddingBottom + textView.paint.fontMetrics.bottom.toInt())
        textView.minWidth = minWidth
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
        windowManager.addView(textView, params)
        textView.setOnTouchListener(MovingTouchListener(params, windowManager))
        textView.isHapticFeedbackEnabled = false
        textView.visibility = View.GONE
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

    private fun displayMemoryDiagnostics() {
        Magnifier.startMonitorMemoryTiming(
            threshold = 500,
            sampleCount = 1,
            onSampleListener = MemoryMonitorListener(this)
        )

        Handler(Looper.getMainLooper()).postDelayed({
            displayMemoryDiagnostics()
        }, 4000)
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

fun initializeMemoryViewFeature(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
        && context.resources.getBoolean(R.bool.diagnostics)) {
        FeatureFlags.registerAdditionalFeature(diagnosticsFeature)
    }
}

private val diagnosticsFeature = FeatureFlagEntry(
    defaultBooleanId = R.bool.diagnostics,
    labelId = R.string.diagnostics,
    onStart = {
        MemoryViewer.getMemoryViewer(it).show()
        FpsDiagnostics.getFpsDiagnostics(it).start()
    },
    onEnd = {
        MemoryViewer.getMemoryViewer(it).hide()
        FpsDiagnostics.getFpsDiagnostics(it).stop()
    }
)