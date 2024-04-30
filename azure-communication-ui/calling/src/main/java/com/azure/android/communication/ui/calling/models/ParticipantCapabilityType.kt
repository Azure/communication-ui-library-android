// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models

internal enum class ParticipantCapabilityType {
    /**
     * Turn Video On
     */
    TURN_VIDEO_ON,

    /**
     * Unmute MICROPHONE
     */
    UNMUTE_MICROPHONE,

    /**
     * Share Screen
     */
    SHARE_SCREEN,

    /**
     * Remove Participant
     */
    REMOVE_PARTICIPANT,

    /**
     * Hang up for everyone
     */
    HANG_UP_FOR_EVERY_ONE,

    /**
     * Add Teams user
     */
    ADD_TEAMS_USER,

    /**
     * Add communications user
     */
    ADD_COMMUNICATION_USER,

    /**
     * Add phone number
     */
    ADD_PHONE_NUMBER,

    /**
     * manage lobby
     */
    MANAGE_LOBBY,

    /**
     * Spotlight participant
     */
    SPOTLIGHT_PARTICIPANT,

    /**
     * Remove participant spotlight
     */
    REMOVE_PARTICIPANT_SPOTLIGHT,

    /**
     * Blur background
     */
    BLUR_BACKGROUND,

    /**
     * Custom background
     */
    CUSTOM_BACKGROUND,

    /**
     * Start live captions
     */
    START_LIVE_CAPTIONS,

    /**
     * Raise hands
     */
    RAISE_HAND,
}
