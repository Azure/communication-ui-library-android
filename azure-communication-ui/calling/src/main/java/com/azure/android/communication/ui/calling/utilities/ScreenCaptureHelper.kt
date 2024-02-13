package com.azure.android.communication.ui.calling.utilities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.azure.android.communication.ui.calling.presentation.CallCompositeActivity
import java.lang.ref.WeakReference

internal class ScreenCaptureHelper(private val activity: CallCompositeActivity) {

    private var mediaProjection: MediaProjection? = null
    private var imageReader: ImageReader? = null
    private var screenCaptureLauncher: ActivityResultLauncher<Intent>? = null
    private var callbackWr : WeakReference<(Bitmap)->Unit>? = null

    internal fun setupScreenCaptureLauncher() {
        screenCaptureLauncher?.let { it.unregister() }
        screenCaptureLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                startScreenCapture(result.resultCode, result.data)
            } else {
                Toast.makeText(activity, "Screen capture permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun requestScreenshot(callback: (Bitmap) -> Unit) {
        val mediaProjectionManager = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
        callbackWr = WeakReference(callback);
        screenCaptureLauncher?.launch(captureIntent)
    }

    private fun startScreenCapture(resultCode: Int, data: Intent?) {
        val mediaProjectionManager = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data!!)

        val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.densityDpi

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2).apply {
            setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage()
                val plane = image.planes[0]
                val buffer = plane.buffer
                buffer.rewind() // Ensure the buffer is at the beginning
                val pixelStride = plane.pixelStride
                val rowStride = plane.rowStride
                val rowPadding = rowStride - pixelStride * width
                val bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
                bitmap.copyPixelsFromBuffer(buffer)

                // Handle the bitmap (e.g., display in ImageView, save to file)
                callbackWr?.get()?.invoke(bitmap)
                callbackWr?.clear()
                callbackWr = null

                image.close()
                cleanup()
            }, Handler(Looper.getMainLooper()))
        }

        mediaProjection?.createVirtualDisplay("ScreenCapture",
            width, height, density, 0,
            imageReader!!.surface, null, null)
    }

    private fun cleanup() {
        mediaProjection?.stop()
        mediaProjection = null
        imageReader?.close()
        imageReader = null
    }
}
