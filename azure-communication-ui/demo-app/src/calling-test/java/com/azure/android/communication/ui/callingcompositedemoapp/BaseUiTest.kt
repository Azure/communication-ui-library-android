// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.os.Build
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

    private val basePermissionList =
        arrayOf(
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.WAKE_LOCK",
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
        )

    init {
        val permissionList =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
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
    }

    @After
    fun tearDown() {
        reportHelper.label("Stopping test")
    }
}

fun <T> Array<T>.append(element: T): Array<T?> {
    val array = copyOf(this.size + 1)
    array[this.size] = element
    return array
}
