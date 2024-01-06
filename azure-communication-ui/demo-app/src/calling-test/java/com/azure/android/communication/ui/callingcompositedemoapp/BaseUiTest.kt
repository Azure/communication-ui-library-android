// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.microsoft.appcenter.espresso.Factory
import com.microsoft.appcenter.espresso.ReportHelper
import leakcanary.DetectLeaksAfterTestSuccess
import org.junit.After
import org.junit.Before
import org.junit.Rule

open class BaseUiTest {
    @get:Rule
    var reportHelper: ReportHelper = Factory.getReportHelper()

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(CallLauncherActivity::class.java)

    @Rule
    @JvmField
    var grantPermissionRule: GrantPermissionRule

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    private val basePermissionList = arrayOf(
        "android.permission.ACCESS_NETWORK_STATE",
        "android.permission.WAKE_LOCK",
        "android.permission.MODIFY_AUDIO_SETTINGS",
        "android.permission.CAMERA",
        "android.permission.RECORD_AUDIO"
    )

    init {
        val permissionList = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            basePermissionList
        } else {
            basePermissionList.append("android.permission.FOREGROUND_SERVICE")
        }
        grantPermissionRule = GrantPermissionRule.grant(*permissionList)
    }

    @Before
    open fun setup() {
        reportHelper.label("Starting test")
        Thread.sleep(5000)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(TELECOM_MANAGER_VALUE_KEY, false)
            .apply()
        context.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(REGISTER_PUSH_ON_EXIT_KEY, false)
            .apply()
    }

    @After
    fun tearDown() {
        reportHelper.label("Stopping test")
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(TELECOM_MANAGER_VALUE_KEY, true)
            .apply()
        context.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(REGISTER_PUSH_ON_EXIT_KEY, true)
            .apply()
    }
}

fun <T> Array<T>.append(element: T): Array<T?> {
    val array = copyOf(this.size + 1)
    array[this.size] = element
    return array
}
