// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.localuser

import android.view.MotionEvent
import android.view.View

internal class DragTouchListener internal constructor() : View.OnTouchListener {
    private var curX = 0f
    private var curY = 0f
    private var startX = 0f
    private var startY = 0f
    private var tranX = 0f
    private var tranY = 0f
    private lateinit var view: View

    fun setView(view: View) {
        this.view = view
    }

    fun reset() {
        view.translationX = 0F
        view.translationY = 0F
    }

    override fun onTouch(
        view: View,
        event: MotionEvent,
    ): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.rawX
                startY = event.rawY
                tranX = view.translationX
                tranY = view.translationY
            }
            MotionEvent.ACTION_MOVE -> {
                curX = event.rawX
                curY = event.rawY
                view.translationX = tranX + (curX - startX)
                view.translationY = tranY + curY - startY
                checkBoundary()
            }
        }
        return true
    }

    private fun checkBoundary() {
        // handle top-left corner
        if (view.x < (view.parent as View).x && view.y < (view.parent as View).y) {
            view.x = (view.parent as View).x
            view.y = (view.parent as View).y
        }
        // handle top-right corner
        if (view.x > ((view.parent as View).width - view.width) && view.y < (view.parent as View).y) {
            view.x = ((view.parent as View).width - view.width).toFloat()
            view.y = (view.parent as View).y
        }
        // handle bottom-left corner
        if (view.x < (view.parent as View).x && view.y > ((view.parent as View).height - view.height)) {
            view.x = (view.parent as View).x
            view.y = ((view.parent as View).height - view.height).toFloat()
        }
        // handle bottom-right corner
        if (view.x > ((view.parent as View).width - view.width) && view.y > ((view.parent as View).height - view.height)) {
            view.x = ((view.parent as View).width - view.width).toFloat()
            view.y = ((view.parent as View).height - view.height).toFloat()
        }
        // handle left boundary
        if (view.x < (view.parent as View).x) {
            view.x = (view.parent as View).x
        }
        // handle top boundary
        if (view.y < (view.parent as View).y) {
            view.y = (view.parent as View).y
        }
        // handle right boundary
        if (view.x > ((view.parent as View).width - view.width)) {
            view.x = ((view.parent as View).width - view.width).toFloat()
        }
        // handle bottom boundary
        if (view.y > ((view.parent as View).height - view.height)) {
            view.y = ((view.parent as View).height - view.height).toFloat()
        }
    }
}
