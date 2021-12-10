// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.factories

import com.azure.android.communication.ui.model.ParticipantInfoModel
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.ParticipantGridCellViewModel

internal class ParticipantGridCellViewModelFactory {
    fun ParticipantGridCellViewModel(participantInfoModel: ParticipantInfoModel): ParticipantGridCellViewModel =
        ParticipantGridCellViewModel(
            participantInfoModel.userIdentifier,
            participantInfoModel.displayName,
            participantInfoModel.cameraVideoStreamModel,
            participantInfoModel.screenShareVideoStreamModel,
            participantInfoModel.isMuted,
            participantInfoModel.isSpeaking,
            participantInfoModel.modifiedTimestamp
        )
}
