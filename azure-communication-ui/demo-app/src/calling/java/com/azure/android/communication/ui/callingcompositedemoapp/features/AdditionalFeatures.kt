// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.features

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import com.azure.android.communication.ui.callingcompositedemoapp.R
import com.azure.android.communication.ui.callingcompositedemoapp.diagnostics.MagnifierViewer

fun conditionallyRegisterDiagnostics(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
        context.resources.getBoolean(R.bool.diagnostics)
    ) {
        FeatureFlags.registerAdditionalFeature(AdditionalFeatures.getDiagnosticFeature(context.applicationContext as Application))
    }
}

class AdditionalFeatures private constructor() {
    companion object {
        private lateinit var diagnosticsFeature: FeatureFlagEntry

        // Diagnostics Feature requires application context
        // It also hooks into life-cycle
        fun getDiagnosticFeature(application: Application): FeatureFlagEntry {
            if (!this::diagnosticsFeature.isInitialized) {
                val magnifierViewer = MagnifierViewer.getMagnifierViewer(application)

                diagnosticsFeature = FeatureFlagEntry(
                    start = {
                        magnifierViewer.show()
                    },
                    end = {
                        magnifierViewer.hide()
                    },
                    enabledByDefault = false,
                    label = "FPS, Memory Diagnostics"
                )

                // Hooks for Activities for this feature
                application.registerActivityLifecycleCallbacks(object :
                        Application.ActivityLifecycleCallbacks {
                        // On Resume/Pause we should show/hide the Overlay
                        // Because after initial enable it goes to accessibility and "resumes"
                        //
                        override fun onActivityResumed(activity: Activity) {
                            if (diagnosticsFeature.active) {
                                magnifierViewer.show()
                            }
                        }

                        override fun onActivityPaused(activity: Activity) {
                            if (diagnosticsFeature.active) {
                                magnifierViewer.hide()
                            }
                        }

                        // Unused
                        override fun onActivityCreated(
                            activity: Activity,
                            savedInstanceState: Bundle?,
                        ) {
                        }

                        override fun onActivityStarted(activity: Activity) {}

                        override fun onActivityStopped(activity: Activity) {}

                        override fun onActivitySaveInstanceState(
                            activity: Activity,
                            outState: Bundle,
                        ) {
                        }

                        override fun onActivityDestroyed(activity: Activity) {}
                    })
            }

            return diagnosticsFeature
        }

        val secondaryThemeFeature = FeatureFlagEntry(
            // Will use default false here
            start = {},
            end = {},
            enabledByDefault = false,
            label = "Secondary theme"
        )
    }
}
