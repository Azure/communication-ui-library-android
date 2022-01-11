package com.azure.android.communication.ui.callingcompositedemoapp.util

import android.view.WindowManager
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class RunWhenScreenOffOrLockedRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {

            override fun evaluate() {
                // Turn screen on
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).wakeUp()

                // Allow any activity to run when locked
                ActivityLifecycleMonitorRegistry
                    .getInstance()
                    .addLifecycleCallback { activity, stage ->
                        if (stage === Stage.PRE_ON_CREATE) {
                            activity.run {
                                setShowWhenLocked(true)
                                setTurnScreenOn(true)
                                window.addFlags(
                                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                )
                            }
                        }
                    }

                // Continue with other statements
                base.evaluate()
            }
        }
    }
}
