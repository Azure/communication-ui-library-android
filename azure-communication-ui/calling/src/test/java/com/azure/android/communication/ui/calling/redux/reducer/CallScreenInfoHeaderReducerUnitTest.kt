// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.CallScreenInfoHeaderAction
import com.azure.android.communication.ui.calling.redux.state.CallScreenInfoHeaderState
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class CallScreenInfoHeaderReducerUnitTest {
    @Test
    fun callScreenInformationHeaderReducer_reduce_on_updateTitleAction() {
        // arrange
        val reducer = CallScreenInformationHeaderReducerImpl()
        val oldState = CallScreenInfoHeaderState(
            null,
            null, /* <CALL_START_TIME> */
            false
            /* </CALL_START_TIME> */
        )
        val title = "title"
        val action = CallScreenInfoHeaderAction.UpdateTitle(title)

        // act
        val updatedState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(title, updatedState.title)
    }

    @Test
    fun callScreenInformationHeaderReducer_reduce_on_updateSubtitleAction() {
        // arrange
        val reducer = CallScreenInformationHeaderReducerImpl()
        val oldState = CallScreenInfoHeaderState(
            null,
            null,
            /* <CALL_START_TIME> */
            false
            /* </CALL_START_TIME> */
        )
        val subtitle = "subtitle"
        val action = CallScreenInfoHeaderAction.UpdateSubtitle(subtitle)

        // act
        val updatedState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(subtitle, updatedState.subtitle)
    }

    /* <CALL_START_TIME> */
    @Test
    fun callScreenInformationHeaderReducer_reduce_on_updateShowCallDuration() {
        // arrange
        val reducer = CallScreenInformationHeaderReducerImpl()
        val oldState = CallScreenInfoHeaderState(null, null, false)
        val showCallDuration = true
        val action = CallScreenInfoHeaderAction.UpdateShowCallDuration(showCallDuration)

        // act
        val updatedState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(showCallDuration, updatedState.showCallDuration)
    }
    /* </CALL_START_TIME> */
}
