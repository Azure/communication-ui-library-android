// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.features

import android.content.Context
import android.content.SharedPreferences
import android.util.LayoutDirection
import com.azure.android.communication.ui.calling.models.CallCompositeSupportedScreenOrientation
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomManagerIntegrationMode
import com.azure.android.communication.ui.callingcompositedemoapp.ADD_CUSTOM_BUTTONS_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.AUDIO_ONLY_MODE_ON
import com.azure.android.communication.ui.callingcompositedemoapp.AUTO_START_CAPTIONS
import com.azure.android.communication.ui.callingcompositedemoapp.AVATAR_IMAGE
import com.azure.android.communication.ui.callingcompositedemoapp.CALL_INFORMATION_DEFAULT_TITLE
import com.azure.android.communication.ui.callingcompositedemoapp.CALL_INFORMATION_SUBTITLE_DEFAULT
import com.azure.android.communication.ui.callingcompositedemoapp.CALL_INFORMATION_SUBTITLE_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.CALL_INFORMATION_SUBTITLE_UPDATE_PARTICIPANT_COUNT_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.CALL_INFORMATION_SUBTITLE_UPDATE_PARTICIPANT_COUNT_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.CALL_INFORMATION_TITLE_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.CALL_INFORMATION_TITLE_UPDATE_PARTICIPANT_COUNT_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.CALL_INFORMATION_TITLE_UPDATE_PARTICIPANT_COUNT_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.CALL_SCREEN_ORIENTATION_SHARED_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.CALL_SUBTITLE
import com.azure.android.communication.ui.callingcompositedemoapp.CALL_TITLE
import com.azure.android.communication.ui.callingcompositedemoapp.CAMERA_ON_BY_DEFAULT_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_ADD_CUSTOM_BUTTONS
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_AUDIO_ONLY_MODE_ON
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_AUTO_START_CAPTIONS
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_CAMERA_ON_BY_DEFAULT_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_DISPLAY_LEAVE_CALL_CONFIRMATION_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_HIDE_CAPTIONS_UI
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_LANGUAGE_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_MIC_ON_BY_DEFAULT_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_RTL_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_SETUP_SCREEN_CAMERA_ENABLED_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_SETUP_SCREEN_MIC_ENABLED_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_SHOW_CALL_DURATION
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_SKIP_SETUP_SCREEN_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_SPOKEN_LANGUAGE
import com.azure.android.communication.ui.callingcompositedemoapp.DEFAULT_SPOKEN_LANGUAGE_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.DISABLE_INTERNAL_PUSH_NOTIFICATIONS
import com.azure.android.communication.ui.callingcompositedemoapp.DISPLAY_DISMISS_BUTTON_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.DISPLAY_DISMISS_BUTTON_KEY_DEFAULT_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.DISPLAY_LEAVE_CALL_CONFIRMATION_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.ENABLE_MULTITASKING
import com.azure.android.communication.ui.callingcompositedemoapp.ENABLE_MULTITASKING_DEFAULT_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.ENABLE_PIP_WHEN_MULTITASKING
import com.azure.android.communication.ui.callingcompositedemoapp.ENABLE_PIP_WHEN_MULTITASKING_DEFAULT_VALUE
import com.azure.android.communication.ui.callingcompositedemoapp.HIDE_CAPTIONS_UI
import com.azure.android.communication.ui.callingcompositedemoapp.LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.MIC_ON_BY_DEFAULT_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.PERSONA_INJECTION_DISPLAY_NAME_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.PERSONA_INJECTION_VALUE_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.RENDERED_DISPLAY_NAME
import com.azure.android.communication.ui.callingcompositedemoapp.SETTINGS_SHARED_PREFS
import com.azure.android.communication.ui.callingcompositedemoapp.SETUP_SCREEN_CAMERA_ENABLED
import com.azure.android.communication.ui.callingcompositedemoapp.SETUP_SCREEN_MIC_ENABLED
import com.azure.android.communication.ui.callingcompositedemoapp.SETUP_SCREEN_ORIENTATION_SHARED_PREF_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.SHOW_CALL_DURATION_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.SKIP_SETUP_SCREEN_VALUE_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.TELECOM_MANAGER_INTEGRATION_OPTION_KEY
import com.azure.android.communication.ui.callingcompositedemoapp.USE_DEPRECATED_LAUNCH_KEY
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.Locale

class SettingsFeatures {

    companion object {
        private lateinit var sharedPrefs: SharedPreferences
        private val defaultLocaleString = Gson().toJson(Locale.US)

        fun initialize(context: Context) {
            sharedPrefs = context.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
        }

        fun language(): String? {
            return sharedPrefs.getString(
                LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                null,
            )
        }

        fun getCaptionsDefaultSpokenLanguage(): String? {
            return sharedPrefs.getString(
                DEFAULT_SPOKEN_LANGUAGE_KEY,
                DEFAULT_SPOKEN_LANGUAGE,
            )
        }
        fun getCallScreenInformationTitleUpdateParticipantCount(): Int {
            return sharedPrefs.getInt(
                CALL_INFORMATION_TITLE_UPDATE_PARTICIPANT_COUNT_KEY,
                CALL_INFORMATION_TITLE_UPDATE_PARTICIPANT_COUNT_VALUE,
            )
        }

        fun getCallScreenInformationSubtitleUpdateParticipantCount(): Int {
            return sharedPrefs.getInt(
                CALL_INFORMATION_SUBTITLE_UPDATE_PARTICIPANT_COUNT_KEY,
                CALL_INFORMATION_SUBTITLE_UPDATE_PARTICIPANT_COUNT_VALUE,
            )
        }

        fun getCallScreenInformationSubtitle(): String? {
            return sharedPrefs.getString(
                CALL_INFORMATION_SUBTITLE_KEY,
                CALL_INFORMATION_SUBTITLE_DEFAULT,
            )
        }

        fun getCallScreenInformationTitle(): String? {
            return sharedPrefs.getString(
                CALL_INFORMATION_TITLE_KEY,
                CALL_INFORMATION_DEFAULT_TITLE,
            )
        }

        fun getCallScreenShowCallDuration(): Boolean? {
            return if (sharedPrefs.contains(SHOW_CALL_DURATION_KEY)) {
                sharedPrefs.getBoolean(
                    SHOW_CALL_DURATION_KEY,
                    DEFAULT_SHOW_CALL_DURATION
                )
            } else {
                null
            }
        }

        fun getLayoutDirection(): Int? {
            val isRTLKey =
                LANGUAGE_ISRTL_VALUE_SHARED_PREF_KEY +
                    sharedPrefs.getString(
                        LANGUAGE_ADAPTER_VALUE_SHARED_PREF_KEY,
                        DEFAULT_LANGUAGE_VALUE,
                    )
            return if (sharedPrefs.contains(isRTLKey)) {
                if (sharedPrefs.getBoolean(isRTLKey, DEFAULT_RTL_VALUE)) {
                    LayoutDirection.RTL
                } else {
                    LayoutDirection.LTR
                }
            } else {
                null
            }
        }

        fun locale(languageDisplayName: String?): Locale? {
            if (languageDisplayName == null) {
                return null
            }

            val localeString = sharedPrefs.getString(languageDisplayName, null)
            return if (localeString != null) {
                GsonBuilder().create().fromJson(localeString, Locale::class.java)
            } else {
                null
            }
        }

        fun orientation(orientationDisplayName: String?): CallCompositeSupportedScreenOrientation? {
            return orientationDisplayName?.let {
                CallCompositeSupportedScreenOrientation.fromString(orientationDisplayName)
            }
        }

        fun displayLanguageName(locale: Locale): String {
            val displayName = locale.displayName
            val localeString = Gson().toJson(locale)
            sharedPrefs.edit().putString(displayName, localeString).apply()
            return displayName
        }

        fun displayOrientationName(orientation: CallCompositeSupportedScreenOrientation): String {
            val displayName = orientation.toString()
            return displayName
        }

        fun displayTelecomManagerOptionName(option: CallCompositeTelecomManagerIntegrationMode): String {
            return option.toString()
        }

        fun getInjectionAvatarForRemoteParticipantSelection(): Boolean {
            return sharedPrefs.getBoolean(PERSONA_INJECTION_VALUE_PREF_KEY, false)
        }

        fun getInjectionDisplayNameRemoteParticipantSelection(): Boolean {
            return sharedPrefs.getBoolean(PERSONA_INJECTION_DISPLAY_NAME_KEY, false)
        }

        fun getSkipSetupScreenFeatureOption(): Boolean? {
            return if (sharedPrefs.contains(SKIP_SETUP_SCREEN_VALUE_KEY)) {
                sharedPrefs.getBoolean(SKIP_SETUP_SCREEN_VALUE_KEY, DEFAULT_SKIP_SETUP_SCREEN_VALUE)
            } else {
                null
            }
        }

        fun getMicOnByDefaultOption(): Boolean? {
            return if (sharedPrefs.contains(MIC_ON_BY_DEFAULT_KEY)) {
                sharedPrefs.getBoolean(MIC_ON_BY_DEFAULT_KEY, DEFAULT_MIC_ON_BY_DEFAULT_VALUE)
            } else {
                null
            }
        }

        fun getCameraOnByDefaultOption(): Boolean? {
            return if (sharedPrefs.contains(CAMERA_ON_BY_DEFAULT_KEY)) {
                sharedPrefs.getBoolean(CAMERA_ON_BY_DEFAULT_KEY, DEFAULT_CAMERA_ON_BY_DEFAULT_VALUE)
            } else {
                null
            }
        }

        fun getAudioOnlyByDefaultOption(): Boolean? {
            return if (sharedPrefs.contains(AUDIO_ONLY_MODE_ON)) {
                sharedPrefs.getBoolean(AUDIO_ONLY_MODE_ON, DEFAULT_AUDIO_ONLY_MODE_ON)
            } else {
                null
            }
        }

        fun getDisplayDismissButtonOption(): Boolean {
            if (!this::sharedPrefs.isInitialized) return false
            return sharedPrefs.getBoolean(DISPLAY_DISMISS_BUTTON_KEY, DISPLAY_DISMISS_BUTTON_KEY_DEFAULT_VALUE)
        }

        fun getDisableInternalPushForIncomingCallCheckbox(): Boolean {
            return sharedPrefs.getBoolean(DISABLE_INTERNAL_PUSH_NOTIFICATIONS, false)
        }

        fun getUseDeprecatedLaunch(): Boolean {
            return sharedPrefs.getBoolean(USE_DEPRECATED_LAUNCH_KEY, false)
        }

        fun getRenderedDisplayNameOption(): String? = sharedPrefs.getString(RENDERED_DISPLAY_NAME, null)

        fun getAvatarImageOption(): String? = sharedPrefs.getString(AVATAR_IMAGE, null)

        fun getTitle(): String? = sharedPrefs.getString(CALL_TITLE, null)

        fun getSubtitle(): String? = sharedPrefs.getString(CALL_SUBTITLE, null)

        fun callScreenOrientation(): String? =
            sharedPrefs.getString(
                CALL_SCREEN_ORIENTATION_SHARED_PREF_KEY,
                null,
            )

        fun telecomManagerIntegration(): String? =
            sharedPrefs.getString(
                TELECOM_MANAGER_INTEGRATION_OPTION_KEY,
                null,
            )

        fun setupScreenOrientation(): String? =
            sharedPrefs.getString(
                SETUP_SCREEN_ORIENTATION_SHARED_PREF_KEY,
                null,
            )

        fun enableMultitasking(): Boolean? {
            return if (sharedPrefs.contains(ENABLE_MULTITASKING)) {
                sharedPrefs.getBoolean(ENABLE_MULTITASKING, ENABLE_MULTITASKING_DEFAULT_VALUE)
            } else {
                null
            }
        }

        fun enablePipWhenMultitasking(): Boolean? {
            return if (sharedPrefs.contains(ENABLE_PIP_WHEN_MULTITASKING)) {
                sharedPrefs.getBoolean(ENABLE_PIP_WHEN_MULTITASKING, ENABLE_PIP_WHEN_MULTITASKING_DEFAULT_VALUE)
            } else {
                null
            }
        }

        fun getDisplayLeaveCallConfirmationValue(): Boolean? {
            return if (sharedPrefs.contains(DISPLAY_LEAVE_CALL_CONFIRMATION_VALUE)) {
                sharedPrefs.getBoolean(
                    DISPLAY_LEAVE_CALL_CONFIRMATION_VALUE,
                    DEFAULT_DISPLAY_LEAVE_CALL_CONFIRMATION_VALUE
                )
            } else {
                null
            }
        }

        fun getSetupScreenCameraEnabledValue(): Boolean? {
            return if (sharedPrefs.contains(SETUP_SCREEN_CAMERA_ENABLED)) {
                sharedPrefs.getBoolean(
                    SETUP_SCREEN_CAMERA_ENABLED,
                    DEFAULT_SETUP_SCREEN_CAMERA_ENABLED_VALUE
                )
            } else {
                null
            }
        }

        fun getSetupScreenMicEnabledValue(): Boolean? {
            return if (sharedPrefs.contains(SETUP_SCREEN_MIC_ENABLED)) {
                sharedPrefs.getBoolean(
                    SETUP_SCREEN_MIC_ENABLED,
                    DEFAULT_SETUP_SCREEN_MIC_ENABLED_VALUE
                )
            } else {
                null
            }
        }

        fun getAutoStartCaptionsEnabled(): Boolean? {
            return if (sharedPrefs.contains(AUTO_START_CAPTIONS)) {
                sharedPrefs.getBoolean(
                    AUTO_START_CAPTIONS,
                    DEFAULT_AUTO_START_CAPTIONS
                )
            } else {
                null
            }
        }

        fun getHideCaptionsUiEnabled(): Boolean? {
            return if (sharedPrefs.contains(HIDE_CAPTIONS_UI)) {
                sharedPrefs.getBoolean(
                    HIDE_CAPTIONS_UI,
                    DEFAULT_HIDE_CAPTIONS_UI
                )
            } else {
                null
            }
        }

        fun getAddCustomButtons(): Boolean? {
            return if (sharedPrefs.contains(ADD_CUSTOM_BUTTONS_KEY)) {
                sharedPrefs.getBoolean(
                    ADD_CUSTOM_BUTTONS_KEY,
                    DEFAULT_ADD_CUSTOM_BUTTONS
                )
            } else {
                null
            }
        }
    }
}
