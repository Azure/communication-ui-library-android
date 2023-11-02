package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeBuilder
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures

class CallCompositeProvider {

    companion object {
        private var instance: CallCompositeProvider? = null

        fun getInstance(): CallCompositeProvider {
            if (instance == null) {
                instance = CallCompositeProvider()
            }
            return instance!!
        }
    }

    private var callComposite: CallComposite? = null

    fun getCallComposite(context: Context): CallComposite {
        if (callComposite == null) {
            callComposite = createCallComposite(context)
        }
        return callComposite!!
    }

    private fun createCallComposite(context: Context): CallComposite {
        SettingsFeatures.initialize(context.applicationContext)

        val selectedLanguage = SettingsFeatures.language()
        val locale = selectedLanguage?.let { SettingsFeatures.locale(it) }
        val selectedCallScreenOrientation = SettingsFeatures.callScreenOrientation()
        val callScreenOrientation = selectedCallScreenOrientation?.let { SettingsFeatures.orientation(it) }
        val selectedSetupScreenOrientation = SettingsFeatures.setupScreenOrientation()
        val setupScreenOrientation = selectedSetupScreenOrientation?.let { SettingsFeatures.orientation(it) }

        val callCompositeBuilder = CallCompositeBuilder()
            .localization(
                CallCompositeLocalizationOptions(
                    locale!!,
                    SettingsFeatures.getLayoutDirection()
                )
            )
            .localization(CallCompositeLocalizationOptions(locale, SettingsFeatures.getLayoutDirection()))
            .setupScreenOrientation(setupScreenOrientation)
            .callScreenOrientation(callScreenOrientation)

        if (AdditionalFeatures.secondaryThemeFeature.active)
            callCompositeBuilder.theme(R.style.MyCompany_Theme_Calling)

        val callComposite = callCompositeBuilder.build()

        // For test purposes we will keep a static ref to CallComposite
        CallLauncherViewModel.callComposite = callComposite
        return callComposite
    }
}
