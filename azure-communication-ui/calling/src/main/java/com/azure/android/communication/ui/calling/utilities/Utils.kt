package com.azure.android.communication.ui.calling.utilities

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

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

internal fun Context.convertDpToPx(dp: Int): Float {
    return this.convertDpToPx(dp.toFloat())
}

internal fun Context.convertDpToPx(dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this.resources.displayMetrics
    )
}

internal fun Activity.isKeyboardOpen(): Boolean {
    val insets = ViewCompat.getRootWindowInsets(this.getRootView())
    return insets?.isVisible(Type.ime()) == true
}

internal fun Activity.hideKeyboard() {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(getRootView().windowToken, 0)
}

internal open class EventFlow {
    protected val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val events: SharedFlow<Unit> = _events
}

internal class MutableEventFlow : EventFlow() {
    fun emit() {
        _events.tryEmit(Unit)
    }
}
