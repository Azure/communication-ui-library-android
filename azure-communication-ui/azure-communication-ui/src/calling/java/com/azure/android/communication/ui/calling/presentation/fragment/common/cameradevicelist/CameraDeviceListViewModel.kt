// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.common.cameradevicelist

import com.azure.android.communication.ui.calling.models.CameraFacing
import com.azure.android.communication.ui.calling.models.VideoDeviceInfoModel
import com.azure.android.communication.ui.calling.models.VideoDeviceType
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.state.CameraDeviceSelection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class CameraDeviceListViewModel(private val dispatch: (Action) -> Unit) {
    private lateinit var cameraListCellStateFlow: MutableStateFlow<List<CameraListCellModel>>
    private val displayCameraListStateFlow = MutableStateFlow(false)
    private var selectedDeviceID = ""

    fun getSelectedDeviceID() = selectedDeviceID

    fun getCameraListCellStateFlow(): StateFlow<List<CameraListCellModel>> {
        return cameraListCellStateFlow
    }

    fun getDisplayCameraListStateFlow(): StateFlow<Boolean> {
        return displayCameraListStateFlow
    }

    fun init(deviceSelection: CameraDeviceSelection) {
        val list: List<CameraListCellModel> =
            deviceSelection.cameras.values.map {
                getCameraListCellModel(it)
            }
        selectedDeviceID = deviceSelection.selectedCameraID
        cameraListCellStateFlow = MutableStateFlow(list)
    }

    fun update(deviceSelection: CameraDeviceSelection) {
        val list: List<CameraListCellModel> =
            deviceSelection.cameras.values.map {
                getCameraListCellModel(it)
            }
        selectedDeviceID = deviceSelection.selectedCameraID
        cameraListCellStateFlow.value = list
    }

    fun displayCameraDeviceSelectionMenu() {
        displayCameraListStateFlow.value = true
    }

    fun closeCameraDeviceSelectionMenu() {
        displayCameraListStateFlow.value = false
    }

    fun selectCameraByID(id: String) {
        dispatch(LocalParticipantAction.CameraChangeTriggered(id))
    }

    private fun getCameraListCellModel(it: VideoDeviceInfoModel): CameraListCellModel {
        return CameraListCellModel(it.name, it.id, it.cameraFacing, it.videoDeviceType)
    }
}

internal data class CameraListCellModel(
    val name: String,
    val id: String,
    val cameraFacing: CameraFacing,
    val videoDeviceType: VideoDeviceType,
)
