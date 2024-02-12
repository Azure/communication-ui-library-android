// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ScreenshotHelper {
    fun captureActivity(activity: Activity): Bitmap {
        // Get the root view of the activity
        val rootView = activity.window.decorView.rootView

        // Create a Bitmap with the view's size
        val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)

        // Draw the view onto the Bitmap
        val canvas = Canvas(bitmap)
        rootView.draw(canvas)
        return bitmap
    }
}
