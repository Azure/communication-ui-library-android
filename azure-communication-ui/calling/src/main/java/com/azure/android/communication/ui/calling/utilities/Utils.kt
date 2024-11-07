package com.azure.android.communication.ui.calling.utilities

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlin.math.roundToInt

internal fun isTablet(context: Context): Boolean {
    return (
            context.resources.configuration.screenLayout
                    and Configuration.SCREENLAYOUT_SIZE_MASK
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE
            )
}

internal fun Activity.getRootView(): View {
    return findViewById(android.R.id.content)
}

internal fun Context.convertDpToPx(dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this.resources.displayMetrics
    )
}

internal fun Activity.isKeyboardOpen(): Boolean {
    val rootView = this.getRootView()
    val heightDiff = rootView.rootView.height - rootView.height
    val marginOfError = this.convertDpToPx(200F).roundToInt()
    return heightDiff > marginOfError
}

internal fun Activity.hideKeyboard() {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(getRootView().windowToken, 0)
}