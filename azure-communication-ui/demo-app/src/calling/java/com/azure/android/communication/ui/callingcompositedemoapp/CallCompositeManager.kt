// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeBuilder
import com.azure.android.communication.ui.calling.models.CallCompositeAudioVideoMode
import com.azure.android.communication.ui.calling.models.CallCompositeButtonViewData
import com.azure.android.communication.ui.calling.models.CallCompositeCallHistoryRecord
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenControlBarOptions
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenHeaderViewData
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenOptions
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateChangedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeCaptionsOptions
import com.azure.android.communication.ui.calling.models.CallCompositeCustomButtonClickEvent
import com.azure.android.communication.ui.calling.models.CallCompositeCustomButtonViewData
import com.azure.android.communication.ui.calling.models.CallCompositeDismissedEvent
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallCancelledEvent
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallEvent
import com.azure.android.communication.ui.calling.models.CallCompositeJoinLocator
import com.azure.android.communication.ui.calling.models.CallCompositeLeaveCallConfirmationMode
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeMultitaskingOptions
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData
import com.azure.android.communication.ui.calling.models.CallCompositePushNotification
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRoomLocator
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenOptions
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenViewData
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingIdLocator
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomManagerIntegrationMode
import com.azure.android.communication.ui.calling.models.CallCompositeTelecomManagerOptions
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.views.DismissCompositeButtonView
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class CallCompositeManager(private val context: Context) {
    val callCompositeCallStateStateFlow = MutableStateFlow("")
    private var callComposite: CallComposite? = null
    private var incomingCallId: String? = null
    /* <CUSTOM_CALL_HEADER> */
    private var callScreenHeaderOptions: CallCompositeCallScreenHeaderViewData? = null
    /* </CUSTOM_CALL_HEADER> */
    private var remoteParticipantsCount = 0

    fun launch(
        applicationContext: Context,
        identity: String,
        acsToken: String,
        displayName: String,
        groupId: UUID?,
        roomId: String?,
        /* <MEETING_ID_LOCATOR> */
        meetingLink: String?,
        meetingId: String?,
        meetingPasscode: String?,
        participantMris: String?,
    ) {
        if (SettingsFeatures.getDisplayDismissButtonOption()) {
            DismissCompositeButtonView.get(applicationContext).show(this)
        } else {
            DismissCompositeButtonView.get(applicationContext).hide()
        }

        createCallCompositeAndSubscribeToEvents(
            applicationContext,
            acsToken,
            displayName,
            identity,
        )

        val localOptions = getLocalOptions(applicationContext)

        val participants = participantMris?.split(",")
        if (!participants.isNullOrEmpty()) {
            if (localOptions == null) {
                callComposite?.launch(applicationContext, participants.map { CommunicationIdentifier.fromRawId(it) })
            } else {
                callComposite?.launch(applicationContext, participants.map { CommunicationIdentifier.fromRawId(it) }, localOptions)
            }
        } else {
            val useDeprecatedLaunch = SettingsFeatures.getUseDeprecatedLaunch()
            val remoteOptions = getRemoteOptions(
                acsToken,
                groupId,
                meetingLink,
                meetingId,
                meetingPasscode,
                roomId,
                displayName,
            )
            val locator = getLocator(
                groupId,
                meetingLink,
                meetingId,
                meetingPasscode,
                roomId,
            )

            if (localOptions == null) {
                if (useDeprecatedLaunch) {
                    callComposite?.launch(applicationContext, remoteOptions)
                } else {
                    callComposite?.launch(applicationContext, locator)
                }
            } else {
                if (useDeprecatedLaunch) {
                    callComposite?.launch(applicationContext, remoteOptions, localOptions)
                } else {
                    callComposite?.launch(applicationContext, locator, localOptions)
                }
            }
        }
    }

    private fun getRemoteOptions(
        acsToken: String,
        groupId: UUID?,
        meetingLink: String?,
        meetingId: String?,
        meetingPasscode: String?,
        roomId: String?,
        displayName: String,
    ): CallCompositeRemoteOptions {
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ acsToken }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val locator: CallCompositeJoinLocator =
            when {
                groupId != null -> CallCompositeGroupCallLocator(groupId)

                !meetingLink.isNullOrEmpty() -> CallCompositeTeamsMeetingLinkLocator(meetingLink)
                !meetingId.isNullOrEmpty() -> CallCompositeTeamsMeetingIdLocator(meetingId, meetingPasscode)
                /* </MEETING_ID_LOCATOR> */
                roomId != null -> CallCompositeRoomLocator(roomId)
                else -> throw IllegalArgumentException("Cannot launch call composite with provided arguments.")
            }

        return CallCompositeRemoteOptions(locator, communicationTokenCredential, displayName)
    }

    private fun getLocator(
        groupId: UUID?,
        meetingLink: String?,
        meetingId: String?,
        meetingPasscode: String?,
        roomId: String?,
    ): CallCompositeJoinLocator {
        val locator: CallCompositeJoinLocator =
            when {
                groupId != null -> CallCompositeGroupCallLocator(groupId)
                !meetingLink.isNullOrEmpty() -> CallCompositeTeamsMeetingLinkLocator(meetingLink)
                !meetingId.isNullOrEmpty() -> CallCompositeTeamsMeetingIdLocator(meetingId, meetingPasscode)
                /* </MEETING_ID_LOCATOR> */
                roomId != null -> CallCompositeRoomLocator(roomId)
                else -> throw IllegalArgumentException("Cannot launch call composite with provided arguments.")
            }

        return locator
    }

    private fun getLocalOptions(context: Context): CallCompositeLocalOptions? {
        val localOptions = CallCompositeLocalOptions()
        var isAnythingChanged = false

        val renderedDisplayName = SettingsFeatures.getRenderedDisplayNameOption()
        var avatarImageBitmap: Bitmap? = null
        SettingsFeatures.getAvatarImageOption()?.let {
            if (it.isNotEmpty()) {
                avatarImageBitmap = BitmapFactory.decodeResource(context.resources, it.toInt())
            }
        }

        if (!renderedDisplayName.isNullOrEmpty() || avatarImageBitmap != null) {
            val participantViewData = CallCompositeParticipantViewData()
            if (renderedDisplayName != null)
                participantViewData.setDisplayName(renderedDisplayName)
            if (avatarImageBitmap != null)
                participantViewData.setAvatarBitmap(avatarImageBitmap)

            localOptions.setParticipantViewData(participantViewData)
            isAnythingChanged = true
        }

        SettingsFeatures.getTitle()?.let { title ->
            if (title.isNotEmpty()) {
                val setupScreenViewData = CallCompositeSetupScreenViewData().setTitle(title)
                SettingsFeatures.getSubtitle()?.let { subTitle ->
                    if (subTitle.isNotEmpty()) {
                        setupScreenViewData.setSubtitle(subTitle)
                    }
                }

                localOptions.setSetupScreenViewData(setupScreenViewData)
                isAnythingChanged = true
            }
        }
        SettingsFeatures.getSkipSetupScreenFeatureOption()?.let {
            localOptions.setSkipSetupScreen(it)
            isAnythingChanged = true
        }
        SettingsFeatures.getAudioOnlyByDefaultOption()?.let {
            localOptions.setAudioVideoMode(
                if (it) {
                    CallCompositeAudioVideoMode.AUDIO_ONLY
                } else {
                    CallCompositeAudioVideoMode.AUDIO_AND_VIDEO
                },
            )
            isAnythingChanged = true
        }
        SettingsFeatures.getCameraOnByDefaultOption()?.let {
            localOptions.setCameraOn(it)
            isAnythingChanged = true
        }
        SettingsFeatures.getMicOnByDefaultOption()?.let {
            localOptions.setMicrophoneOn(it)
            isAnythingChanged = true
        }

        val autoStartCaptions = SettingsFeatures.getAutoStartCaptionsEnabled()
        val defaultSpokenLanguage = SettingsFeatures.getCaptionsDefaultSpokenLanguage()

        if (autoStartCaptions != null || defaultSpokenLanguage?.isNotEmpty() == true) {
            val captionsViewData =
                CallCompositeCaptionsOptions()

            autoStartCaptions.let {
                if (it == true) {
                    captionsViewData.setCaptionsOn(true)
                }
            }

            defaultSpokenLanguage.let {
                captionsViewData.setSpokenLanguage(it)
            }

            localOptions.setCaptionsOptions(captionsViewData)
            isAnythingChanged = true
        }

        callScreenOptions().let {
            localOptions.callScreenOptions = it
            isAnythingChanged = true
        }

        setupScreenOptions()?.let {
            localOptions.setupScreenOptions = it
            isAnythingChanged = true
        }

        /* <END_CALL_FOR_ALL>
        localOptions.isOnCallEndTerminateForAll = true
        isAnythingChanged = true
        </END_CALL_FOR_ALL> */

        return if (isAnythingChanged) localOptions else null
    }

    private fun subscribeToEvents(
        context: Context,
        callComposite: CallComposite,
    ) {
        callComposite.addOnErrorEventHandler(CallLauncherActivityErrorHandler(context, callComposite))

        val callStateEventHandler: ((CallCompositeCallStateChangedEvent) -> Unit) = {
            callCompositeCallStateStateFlow.value = it.code.toString()
            toast(context, "Call State: ${it.code}.")
        }

        callComposite.addOnCallStateChangedEventHandler(callStateEventHandler)

        callComposite.addOnUserReportedEventHandler {
            toast(context, "onUserReportedEvent: ${it.userMessage}")
        }
        callComposite.addOnUserReportedEventHandler(UserReportedIssueHandler(context.applicationContext as Application))

        val onDismissedEventHandler: ((CallCompositeDismissedEvent) -> Unit) = {
            remoteParticipantsCount = 0
            toast(
                context,
                "onDismissed: errorCode: ${it.errorCode}, cause: ${it.cause?.message}"
            )
        }
        callComposite.addOnDismissedEventHandler(onDismissedEventHandler)

        /* <CUSTOM_CALL_HEADER> */
        callComposite.addOnRemoteParticipantLeftEventHandler { event ->
            toast(context, "Remote participant removed: ${event.identifiers.count()}")
            event.identifiers.forEach {
                Log.d(CallLauncherActivity.TAG, "Remote participant removed: ${it.rawId}")
            }
        }
        /* </CUSTOM_CALL_HEADER> */

        callComposite.addOnPictureInPictureChangedEventHandler {
            toast(context, "isInPictureInPicture: " + it.isInPictureInPicture)
        }

        callComposite.addOnRemoteParticipantJoinedEventHandler { event ->
            toast(context, message = "Joined ${event.identifiers.count()} remote participants")
            event.identifiers.forEach {
                Log.d(CallLauncherActivity.TAG, "Remote participant joined: ${it.rawId}")
            }
            remoteParticipantsCount += event.identifiers.count()
            /* <CUSTOM_CALL_HEADER> */
            val titleUpdateCount = SettingsFeatures.getCallScreenInformationTitleUpdateParticipantCount()
            if (titleUpdateCount != 0 && titleUpdateCount <= remoteParticipantsCount) {
                callScreenHeaderOptions?.let {
                    it.title = "Custom Call Screen Header: $remoteParticipantsCount participants"
                }
            }
            val subtitleUpdateCount = SettingsFeatures.getCallScreenInformationSubtitleUpdateParticipantCount()
            if (subtitleUpdateCount != 0 && subtitleUpdateCount <= remoteParticipantsCount) {
                callScreenHeaderOptions?.let {
                    it.subtitle = "Custom Call Screen Header: $remoteParticipantsCount participants"
                }
            }
            /* </CUSTOM_CALL_HEADER> */
        }

        callComposite.addOnAudioSelectionChangedEventHandler { event ->
            toast(context, message = "Audio selection changed to ${event.audioSelectionMode}")
        }

        callComposite.addOnIncomingCallEventHandler {
            toast(context, "Incoming call. ${it.callId}")
            onIncomingCall(it)
        }

        callComposite.addOnIncomingCallCancelledEventHandler {
            toast(context, "Incoming call cancelled. ${it.callId}")
            onIncomingCallCancelled(it)
        }

        if (SettingsFeatures.getInjectionAvatarForRemoteParticipantSelection()) {
            callComposite.addOnRemoteParticipantJoinedEventHandler(
                RemoteParticipantJoinedHandler(callComposite, context)
            )
        }
    }

    fun dismissCallComposite() {
        callComposite?.dismiss()
        callComposite = null
    }

    fun bringCallCompositeToForeground(context: Context) {
        callComposite?.bringToForeground(context)
    }

    fun getCallHistory(context: Context, acsToken: String, displayName: String): List<CallCompositeCallHistoryRecord>? {
        createCallCompositeAndSubscribeToEvents(context, acsToken, displayName)
        return callComposite?.getDebugInfo(context)?.callHistoryRecords
    }

    fun acceptIncomingCall(applicationContext: Context, acsToken: String, displayName: String) {
        hideIncomingCallNotification()
        createCallCompositeAndSubscribeToEvents(applicationContext, acsToken, displayName)
        val localOptions = getLocalOptions(applicationContext)
        if (localOptions == null) {
            callComposite?.accept(applicationContext, incomingCallId)
        } else {
            callComposite?.accept(applicationContext, incomingCallId, localOptions)
        }
    }

    fun declineIncomingCall() {
        hideIncomingCallNotification()
        callComposite?.reject(incomingCallId)
    }

    fun hold() {
        callComposite?.hold()
    }

    fun resume() {
        callComposite?.resume()
    }

    fun hideIncomingCallNotification() {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(1)
    }

    fun handleIncomingCall(
        value: Map<String, String>,
        acsIdentityToken: String,
        displayName: String,
        applicationContext: Context
    ) {
        createCallCompositeAndSubscribeToEvents(
            applicationContext,
            acsIdentityToken,
            displayName
        )
        if (callComposite?.callState == CallCompositeCallStateCode.CONNECTED) {
            toast(applicationContext, "Incoming call ignored as there is already an active call.")
            return
        }
        callComposite?.handlePushNotification(CallCompositePushNotification(value))
    }

    fun registerPush(applicationContext: Context, acsToken: String, displayName: String) {
        createCallCompositeAndSubscribeToEvents(applicationContext, acsToken, displayName)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                throw task.exception ?: IllegalStateException("Failed to get Firebase token")
            }
            val deviceRegistrationToken = task.result
            try {
                callComposite?.registerPushNotification(deviceRegistrationToken)
                    ?.whenComplete { _, throwable ->
                        if (throwable != null) {
                            toast(applicationContext, "Register push failed.")
                            throw throwable
                        } else {
                            toast(applicationContext, "Register push success.")
                        }
                    }
            } catch (e: Exception) {
                e.message?.let { toast(applicationContext, it) }
            }
        }
    }

    fun unregisterPush(applicationContext: Context, acsToken: String, displayName: String) {
        createCallCompositeAndSubscribeToEvents(applicationContext, acsToken, displayName)
        try {
            callComposite?.unregisterPushNotification()
                ?.whenComplete { _, throwable ->
                    if (throwable != null) {
                        toast(applicationContext, "Unregister push failed.")
                        throw throwable
                    } else {
                        toast(applicationContext, "Unregister push success.")
                    }
                }
        } catch (e: Exception) {
            e.message?.let { toast(applicationContext, it) }
        }
    }

    private fun createCallCompositeAndSubscribeToEvents(
        context: Context,
        acsToken: String,
        displayName: String,
        identity: String = "",
    ) {
        if (this.callComposite != null) {
            return
        }
        val callComposite = createCallComposite(acsToken, displayName, context, identity)
        subscribeToEvents(context, callComposite)
        this.callComposite = callComposite
    }

    private fun onIncomingCall(incomingCall: CallCompositeIncomingCallEvent) {
        incomingCallId = incomingCall.callId
        showNotificationForIncomingCall(incomingCall)
    }

    private fun onIncomingCallCancelled(callCancelled: CallCompositeIncomingCallCancelledEvent) {
        incomingCallId = null
        hideIncomingCallNotification()
    }

    private fun createCallComposite(
        acsToken: String,
        displayName: String,
        context: Context,
        identity: String
    ): CallComposite {
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ acsToken }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val callScreenOrientation =
            SettingsFeatures.orientation(SettingsFeatures.callScreenOrientation())
        val setupScreenOrientation =
            SettingsFeatures.orientation(SettingsFeatures.setupScreenOrientation())

        val callCompositeBuilder = CallCompositeBuilder()

        if (identity.isNotEmpty()) {
            callCompositeBuilder.userId(CommunicationIdentifier.fromRawId(identity))
        }

        if (setupScreenOrientation != null) {
            callCompositeBuilder.setupScreenOrientation(setupScreenOrientation)
        }

        if (SettingsFeatures.getDisableInternalPushForIncomingCallCheckbox()) {
            callCompositeBuilder.disableInternalPushForIncomingCall(true)
        }

        if (callScreenOrientation != null) {
            callCompositeBuilder.callScreenOrientation(callScreenOrientation)
        }

        SettingsFeatures.locale(SettingsFeatures.language())?.let {
            val isRtl = SettingsFeatures.getLayoutDirection()
            callCompositeBuilder
                .localization(
                    if (isRtl != null) {
                        CallCompositeLocalizationOptions(it, isRtl)
                    } else {
                        CallCompositeLocalizationOptions(it)
                    },
                )
        }

        if (!SettingsFeatures.getUseDeprecatedLaunch()) {
            callCompositeBuilder.credential(communicationTokenCredential)
            callCompositeBuilder.applicationContext(context)
            callCompositeBuilder.displayName(displayName)
        }

        SettingsFeatures.telecomManagerIntegration()?.let {
            if (it != DEFAULT_TELECOM_MANAGER_INTEGRATION_OPTION) {
                val telecomManagerOptions = if (CallCompositeTelecomManagerIntegrationMode.fromString(it)
                    == CallCompositeTelecomManagerIntegrationMode.APPLICATION_IMPLEMENTED_TELECOM_MANAGER
                ) {
                    CallCompositeTelecomManagerOptions(CallCompositeTelecomManagerIntegrationMode.APPLICATION_IMPLEMENTED_TELECOM_MANAGER)
                } else if (CallCompositeTelecomManagerIntegrationMode.fromString(it)
                    == CallCompositeTelecomManagerIntegrationMode.SDK_PROVIDED_TELECOM_MANAGER
                ) {
                    CallCompositeTelecomManagerOptions(
                        CallCompositeTelecomManagerIntegrationMode.SDK_PROVIDED_TELECOM_MANAGER,
                        BuildConfig.APPLICATION_ID
                    )
                } else {
                    null
                }
                telecomManagerOptions?.let { option ->
                    callCompositeBuilder.telecomManagerOptions(option)
                }
            }
        }

        callScreenOptions().let { callCompositeBuilder.callScreenOptions(it) }

        setupScreenOptions()?.let { callCompositeBuilder.setupScreenOptions(it) }

        if (AdditionalFeatures.secondaryThemeFeature.active) {
            callCompositeBuilder.theme(R.style.MyCompany_Theme_Calling)
        }

        if (SettingsFeatures.enableMultitasking() != null) {
            val multitaskingOptions =
                if (SettingsFeatures.enablePipWhenMultitasking() != null) {
                    CallCompositeMultitaskingOptions(
                        SettingsFeatures.enableMultitasking(),
                        SettingsFeatures.enablePipWhenMultitasking(),
                    )
                } else {
                    CallCompositeMultitaskingOptions(
                        SettingsFeatures.enableMultitasking(),
                    )
                }

            callCompositeBuilder.multitasking(multitaskingOptions)
        }

        return callCompositeBuilder.build()
    }

    private fun callScreenOptions(): CallCompositeCallScreenOptions? {
        var callScreenOptions: CallCompositeCallScreenOptions? = null
        if (SettingsFeatures.getDisplayLeaveCallConfirmationValue() != null) {
            callScreenOptions = CallCompositeCallScreenOptions()

            val controlBarOptions = CallCompositeCallScreenControlBarOptions()
            callScreenOptions.setControlBarOptions(controlBarOptions)

            controlBarOptions.setLeaveCallConfirmation(
                if (SettingsFeatures.getDisplayLeaveCallConfirmationValue() == true) CallCompositeLeaveCallConfirmationMode.ALWAYS_ENABLED
                else CallCompositeLeaveCallConfirmationMode.ALWAYS_DISABLED
            )
        }

        if (SettingsFeatures.getAddCustomButtons() == true) {
            callScreenOptions = callScreenOptions ?: CallCompositeCallScreenOptions()
            if (callScreenOptions.controlBarOptions == null)
                callScreenOptions.controlBarOptions = CallCompositeCallScreenControlBarOptions()

            with(callScreenOptions) {
                controlBarOptions.cameraButton = CallCompositeButtonViewData()
                    .setOnClickHandler { toast(it.context, "cameraButton clicked") }

                controlBarOptions.microphoneButton = CallCompositeButtonViewData()
                    .setOnClickHandler { toast(it.context, "microphoneButton clicked") }

                controlBarOptions.audioDeviceButton = CallCompositeButtonViewData()
                    .setOnClickHandler { toast(it.context, "audioDeviceButton clicked") }

                controlBarOptions.liveCaptionsButton = CallCompositeButtonViewData()
                    .setOnClickHandler { toast(it.context, "liveCaptionsButton clicked") }

                controlBarOptions.liveCaptionsToggleButton = CallCompositeButtonViewData()
                    .setOnClickHandler { toast(it.context, "liveCaptionsToggleButton clicked") }

                controlBarOptions.spokenLanguageButton = CallCompositeButtonViewData()
                    .setOnClickHandler { toast(it.context, "spokenLanguageButton clicked") }

                controlBarOptions.captionsLanguageButton = CallCompositeButtonViewData()
                    .setOnClickHandler { toast(it.context, "captionsLanguageButton clicked") }

                controlBarOptions.reportIssueButton = CallCompositeButtonViewData()
                    .setOnClickHandler { toast(it.context, "reportIssueButton clicked") }

                controlBarOptions.shareDiagnosticsButton = CallCompositeButtonViewData()
                    .setOnClickHandler { toast(it.context, "shareDiagnosticsButton clicked") }

                if (controlBarOptions == null)
                    controlBarOptions = CallCompositeCallScreenControlBarOptions()

                val troubleshootingTipsButton =
                    CallCompositeCustomButtonViewData(
                        UUID.randomUUID().toString(),
                        R.drawable.ic_fluent_arrow_next_24_regular,
                        "Troubleshooting tips",
                        fun(it: CallCompositeCustomButtonClickEvent) {
                            val intent = Intent(it.context, TestActivity::class.java)
                            context.startActivity(intent)
                        }
                    )

                val disableButtonsCustomButton =
                    CallCompositeCustomButtonViewData(
                        UUID.randomUUID().toString(),
                        R.drawable.image_koala,
                        "Enable/disable buttons",
                        fun(_: CallCompositeCustomButtonClickEvent) {
                            troubleshootingTipsButton.isEnabled = !troubleshootingTipsButton.isEnabled

                            controlBarOptions.cameraButton.isEnabled =
                                !controlBarOptions.cameraButton.isEnabled
                            controlBarOptions.microphoneButton.isEnabled =
                                !controlBarOptions.microphoneButton.isEnabled
                            controlBarOptions.audioDeviceButton.isEnabled =
                                !controlBarOptions.audioDeviceButton.isEnabled
                            controlBarOptions.liveCaptionsButton.isEnabled =
                                !controlBarOptions.liveCaptionsButton.isEnabled
                            controlBarOptions.liveCaptionsToggleButton.isEnabled =
                                !controlBarOptions.liveCaptionsToggleButton.isEnabled
                            controlBarOptions.spokenLanguageButton.isEnabled =
                                !controlBarOptions.spokenLanguageButton.isEnabled
                            controlBarOptions.captionsLanguageButton.isEnabled =
                                !controlBarOptions.captionsLanguageButton.isEnabled
                            controlBarOptions.shareDiagnosticsButton.isEnabled =
                                !controlBarOptions.shareDiagnosticsButton.isEnabled
                            controlBarOptions.reportIssueButton.isEnabled =
                                !controlBarOptions.reportIssueButton.isEnabled
                        }
                    )

                val hideCustomButton =
                    CallCompositeCustomButtonViewData(
                        UUID.randomUUID().toString(),
                        R.drawable.image_koala,
                        "Hide/show buttons",
                        fun(_: CallCompositeCustomButtonClickEvent) {
                            troubleshootingTipsButton.isVisible = !troubleshootingTipsButton.isVisible

                            controlBarOptions.cameraButton.isVisible =
                                !controlBarOptions.cameraButton.isVisible
                            controlBarOptions.microphoneButton.isVisible =
                                !controlBarOptions.microphoneButton.isVisible
                            controlBarOptions.audioDeviceButton.isVisible =
                                !controlBarOptions.audioDeviceButton.isVisible
                            controlBarOptions.liveCaptionsButton.isVisible =
                                !controlBarOptions.liveCaptionsButton.isVisible
                            controlBarOptions.liveCaptionsToggleButton.isVisible =
                                !controlBarOptions.liveCaptionsToggleButton.isVisible
                            controlBarOptions.spokenLanguageButton.isVisible =
                                !controlBarOptions.spokenLanguageButton.isVisible
                            controlBarOptions.captionsLanguageButton.isVisible =
                                !controlBarOptions.captionsLanguageButton.isVisible
                            controlBarOptions.shareDiagnosticsButton.isVisible =
                                !controlBarOptions.shareDiagnosticsButton.isVisible
                            controlBarOptions.reportIssueButton.isVisible =
                                !controlBarOptions.reportIssueButton.isVisible
                        }
                    )

                controlBarOptions.setCustomButtons(
                    listOf(troubleshootingTipsButton, disableButtonsCustomButton, hideCustomButton)
                )
            }
        }
        /* <CUSTOM_CALL_HEADER> */
        if (!SettingsFeatures.getCallScreenInformationTitle().isNullOrEmpty() ||
            !SettingsFeatures.getCallScreenInformationSubtitle().isNullOrEmpty() ||
            SettingsFeatures.getCallScreenInformationTitleUpdateParticipantCount() != 0 ||
            SettingsFeatures.getCallScreenInformationSubtitleUpdateParticipantCount() != 0
        ) {
            callScreenOptions = callScreenOptions ?: CallCompositeCallScreenOptions()

            callScreenHeaderOptions =
                CallCompositeCallScreenHeaderViewData()
            SettingsFeatures.getCallScreenInformationTitle()?.let {
                if (it.isNotEmpty()) {
                    callScreenHeaderOptions?.title = it
                }
            }
            SettingsFeatures.getCallScreenInformationSubtitle()?.let {
                if (it.isNotEmpty()) {
                    callScreenHeaderOptions?.subtitle = it
                }
            }
            callScreenOptions.setHeaderViewData(callScreenHeaderOptions)
        }
        /* </CUSTOM_CALL_HEADER> */
        return callScreenOptions
    }

    private fun setupScreenOptions(): CallCompositeSetupScreenOptions? {

        var setupScreenOptions: CallCompositeSetupScreenOptions? = null

        if (SettingsFeatures.getSetupScreenCameraEnabledValue() != null) {
            setupScreenOptions = CallCompositeSetupScreenOptions()
            setupScreenOptions.setCameraButtonEnabled(SettingsFeatures.getSetupScreenCameraEnabledValue())
        }

        if (SettingsFeatures.getSetupScreenMicEnabledValue() != null) {
            setupScreenOptions = setupScreenOptions ?: CallCompositeSetupScreenOptions()
            setupScreenOptions.setMicrophoneButtonEnabled(SettingsFeatures.getSetupScreenMicEnabledValue())
        }

        if (SettingsFeatures.getAddCustomButtons() == true) {
            setupScreenOptions = setupScreenOptions ?: CallCompositeSetupScreenOptions()
            setupScreenOptions.setCameraButton(
                CallCompositeButtonViewData()
                    .setOnClickHandler { toast(it.context, "CameraButton clicked") }
            )

            setupScreenOptions.setMicrophoneButton(
                CallCompositeButtonViewData()
                    .setOnClickHandler { toast(it.context, "MicrophoneButton clicked") }
            )

            setupScreenOptions.setAudioDeviceButton(
                CallCompositeButtonViewData()
                    .setOnClickHandler { toast(it.context, "AudioDeviceButton clicked") }
            )
        }

        return setupScreenOptions
    }

    private fun toast(
        context: Context,
        message: String,
    ) {
        Log.i("ACSCallingUI", message)
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context.applicationContext, "Debug: $message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNotificationForIncomingCall(notification: CallCompositeIncomingCallEvent) {
        context.let { context ->
            Log.i(CallLauncherActivity.TAG, "Showing notification for incoming call")

            val resultIntent = Intent(context, CallLauncherActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                action = IntentHelper.INCOMING_CALL
            }
            val resultPendingIntent = PendingIntent.getActivity(
                context, 0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val answerIntent = Intent(context, CallLauncherActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                action = IntentHelper.ANSWER
                putExtra("action", IntentHelper.ANSWER)
            }
            val answerCallPendingIntent = PendingIntent.getActivity(
                context, 0,
                answerIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val declineIntent = Intent(context, CallLauncherActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                action = IntentHelper.DECLINE
                putExtra("action", IntentHelper.DECLINE)
            }
            val declineCallPendingIntent = PendingIntent.getActivity(
                context, 0,
                declineIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val content = java.lang.String.format(
                "%s",
                notification.callerDisplayName
            )

            val intent = Intent(context, IncomingCallActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            intent.putExtra(IncomingCallActivity.DISPLAY_NAME, notification.callerDisplayName)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(context, "acs")
                    .setContentIntent(resultPendingIntent)
                    .setSmallIcon(android.R.drawable.ic_menu_call)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("Incoming Call")
                    .setContentText(content)
                    .addAction(
                        android.R.drawable.ic_menu_call,
                        context.getString(R.string.accept),
                        answerCallPendingIntent
                    )
                    .addAction(
                        android.R.drawable.ic_menu_call,
                        context.getString(R.string.decline),
                        declineCallPendingIntent
                    )
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setPriority(NotificationManagerCompat.IMPORTANCE_MAX)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setFullScreenIntent(pendingIntent, true)
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(1, builder.build())
        }
    }
}
