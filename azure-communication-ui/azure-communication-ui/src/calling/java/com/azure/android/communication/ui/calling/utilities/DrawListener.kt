/*
 * *
 *  * Copyright (c) Microsoft Corporation. All rights reserved.
 *  * Licensed under the MIT License.
 *
 */

package com.azure.android.communication.ui.calling.utilities

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver

internal class DrawListener : ViewTreeObserver.OnDrawListener {

    private var mainHandler: Handler? = null
    private var view: View? = null
    private var onDrawCallback: OnDrawCallback? = null

    private var onDrawInvoked = false

    interface OnDrawCallback {
        fun onDrawingStart()
        fun onDrawingFinish()
    }

    private constructor(view: View, onDrawCallback: OnDrawCallback) : super() {
        this.view = view
        this.onDrawCallback = onDrawCallback
        mainHandler = Handler(Looper.getMainLooper())
        registerDrawListener()
    }

    companion object {
        fun registerDrawListener(
            view: View,
            onDrawCallback: OnDrawCallback
        ): DrawListener {
            return DrawListener(view, onDrawCallback)
        }
    }

    private fun registerDrawListener() {
        if (view!!.viewTreeObserver.isAlive && view!!.isAttachedToWindow) {
            view!!.viewTreeObserver.addOnDrawListener(this@DrawListener)
        } else {
            // Workaround for a bug fixed in API 26
            // https://android.googlesource.com/platform/frameworks/base/+/9f8ec54244a5e0343b9748db3329733f259604f3
            view!!.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    if (view!!.viewTreeObserver.isAlive) {
                        view!!.viewTreeObserver.addOnDrawListener(this@DrawListener)
                    }

                    // We only want to listen to this event for the first time only
                    view!!.removeOnAttachStateChangeListener(this)
                }

                override fun onViewDetachedFromWindow(v: View) {
                    // No-op
                }
            })
        }
    }

    override fun onDraw() {
        if (!onDrawInvoked) {
            onDrawInvoked = true

            onDrawCallback?.onDrawingStart()

            onDrawCallback!!::onDrawingFinish?.let { mainHandler!!.postAtFrontOfQueue(it) }

            mainHandler!!.post {
                if (view!!.viewTreeObserver.isAlive) {
                    view!!.viewTreeObserver.removeOnDrawListener(this@DrawListener)
                }
            }
        }
    }
}
