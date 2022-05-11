// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.calling.localuser

import android.view.MotionEvent
import android.view.View

class DragTouchListener : View.OnTouchListener {

    private var curX = 0f
    private var curY = 0f
    private var startX = 0f
    private var startY = 0f
    private var tranX = 0f
    private var tranY = 0f
    private lateinit var view: View
    private var listener: OnDragListener? = null
    private var isFinish = false

    internal constructor() {}

    fun setView(view: View) {
        this.view = view
    }

    fun reset() {
        view.translationX = 0F
        view.translationY = 0F
    }

    interface OnDragListener {
        fun onDragging(view: View?)
        fun onDragFinish(view: View?)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.rawX
                startY = event.rawY
                tranX = view.translationX
                tranY = view.translationY

                isFinish = false
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isFinish) {
                    curX = event.rawX
                    curY = event.rawY
                    view.translationX = tranX + (curX - startX)
                    view.translationY = tranY + curY - startY

                    if (listener != null) {
                        listener!!.onDragging(view)
                    }
                    if (!isInAllowDistance() && listener != null) {
                        listener!!.onDragFinish(view)
                        isFinish = true
                    }
                }
            }
            MotionEvent.ACTION_UP -> if (listener != null && !isFinish) {
                listener!!.onDragFinish(view)
                isFinish = true
            }
        }
        return true
    }

    private fun isInAllowDistance(): Boolean {
        // handle top-left corner
        if (view.x < (view.parent as View).x && view.y < (view.parent as View).y) {
            view.x = (view.parent as View).x
            view.y = (view.parent as View).y
            return false
        }
        // handle top-right corner
        if (view.x > ((view.parent as View).width - view.width) && view.y < (view.parent as View).y) {
            view.x = ((view.parent as View).width - view.width).toFloat()
            view.y = (view.parent as View).y
            return false
        }
        // handle bottom-left corner
        if (view.x < (view.parent as View).x && view.y > ((view.parent as View).height - view.height)) {
            view.x = (view.parent as View).x
            view.y = ((view.parent as View).height - view.height).toFloat()
            return false
        }
        // handle bottom-right corner
        if (view.x > ((view.parent as View).width - view.width) && view.y > ((view.parent as View).height - view.height)) {
            view.x = ((view.parent as View).width - view.width).toFloat()
            view.y = ((view.parent as View).height - view.height).toFloat()
            return false
        }
        // handle left boundary
        if (view.x < (view.parent as View).x) {
            view.x = (view.parent as View).x
            return false
        }
        // handle top boundary
        if (view.y < (view.parent as View).y) {
            view.y = (view.parent as View).y
            return false
        }
        // handle right boundary
        if (view.x > ((view.parent as View).width - view.width)) {
            view.x = ((view.parent as View).width - view.width).toFloat()
            return false
        }
        // handle bottom boundary
        if (view.y > ((view.parent as View).height - view.height)) {
            view.y = ((view.parent as View).height - view.height).toFloat()
            return false
        }
        return true
    }
}
