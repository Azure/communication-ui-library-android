package com.azure.android.communication.ui.calling.presentation.manager

import com.azure.android.communication.ui.calling.configuration.CallCompositeConfiguration
import com.azure.android.communication.ui.calling.models.setEnabledChangedEventHandler
import com.azure.android.communication.ui.calling.models.setSubtitleChangedEventHandler
import com.azure.android.communication.ui.calling.models.setTitleChangedEventHandler
import com.azure.android.communication.ui.calling.models.setVisibleChangedEventHandler
import com.azure.android.communication.ui.calling.redux.Store
import com.azure.android.communication.ui.calling.redux.action.ButtonOptionsAction
import com.azure.android.communication.ui.calling.redux.action.CallScreenInfoHeaderAction
import com.azure.android.communication.ui.calling.redux.state.ReduxState

internal class UpdatableOptionsManager (
    private val configuration: CallCompositeConfiguration,
    private val store: Store<ReduxState>,
) {
    fun start() {
        /* <CUSTOM_CALL_HEADER> */
        configuration.callScreenOptions?.headerOptions?.run {
            setTitleChangedEventHandler {
                store.dispatch(CallScreenInfoHeaderAction.UpdateTitle(it))
            }
            setSubtitleChangedEventHandler {
                store.dispatch(CallScreenInfoHeaderAction.UpdateSubtitle(it))
            }
        }
        /* </CUSTOM_CALL_HEADER> */
        configuration.callScreenOptions?.controlBarOptions?.run {
            cameraButton?.setEnabledChangedEventHandler {
                store.dispatch(ButtonOptionsAction.CallScreenCameraButtonIsEnabledUpdated(it))
            }
            cameraButton?.setVisibleChangedEventHandler {
                store.dispatch(ButtonOptionsAction.CallScreenCameraButtonIsVisibleUpdated(it))
            }
        }
    }
}