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
    fun captureActivity(activity: Activity): File? {
        // Get the root view of the activity
        val rootView = activity.window.decorView.rootView

        // Create a Bitmap with the view's size
        val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)

        // Draw the view onto the Bitmap
        val canvas = Canvas(bitmap)
        rootView.draw(canvas)

        // Format the current date and time for the filename
        val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val timestamp = formatter.format(Date())

        // Define the file where the screenshot will be saved
        val screenshotFile = File(
            activity.cacheDir,
            "screenshot_$timestamp.png"
        )
        var fileOutputStream: FileOutputStream? = null
        return try {
            fileOutputStream = FileOutputStream(screenshotFile)
            // Compress the bitmap and write to the output stream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)

            // Return the file
            screenshotFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try {
                fileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
