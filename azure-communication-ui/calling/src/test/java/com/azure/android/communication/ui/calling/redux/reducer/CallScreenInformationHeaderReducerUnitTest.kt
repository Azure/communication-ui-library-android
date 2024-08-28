// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
/* <CUSTOM_CALL_HEADER> */

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.CallScreenInformationHeaderAction
import com.azure.android.communication.ui.calling.redux.state.CallScreenInformationHeaderState
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class CallScreenInformationHeaderReducerUnitTest {
    @Test
    fun callScreenInformationHeaderReducer_reduce_on_updateTitleAction() {
        // arrange
        val reducer = CallScreenInformationHeaderReducerImpl()
        val oldState = CallScreenInformationHeaderState(null, null)
        val title = "title"
        val action = CallScreenInformationHeaderAction.UpdateTitle(title)

        // act
        val updatedState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(title, updatedState.title)
    }

    @Test
    fun callScreenInformationHeaderReducer_reduce_on_updateSubtitleAction() {
        // arrange
        val reducer = CallScreenInformationHeaderReducerImpl()
        val oldState = CallScreenInformationHeaderState(null, null)
        val subtitle = "subtitle"
        val action = CallScreenInformationHeaderAction.UpdateSubtitle(subtitle)

        // act
        val updatedState = reducer.reduce(oldState, action)

        // assert
        Assert.assertEquals(subtitle, updatedState.subtitle)
    }
}

/* </CUSTOM_CALL_HEADER> */
