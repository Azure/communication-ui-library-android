// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.utilities

import android.os.Handler
import android.os.HandlerThread

internal class StoreHandlerThread {
    private var handlerThread: HandlerThread = HandlerThread("StoreHandlerThread")
    private lateinit var handler: Handler

    fun startHandlerThread(): Handler {
        handlerThread.let {
            it.start()
            handler = Handler(it.looper)
        }
        return handler
    }

    fun isHandlerThreadAlive(): Boolean {
        return handler.looper.thread.isAlive
    }

    fun stopHandlerThread() {
        handlerThread.quit()
    }
}
