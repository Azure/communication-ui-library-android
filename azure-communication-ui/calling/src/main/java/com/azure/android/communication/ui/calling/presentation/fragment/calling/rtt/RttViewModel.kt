// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

/* <RTT_POC> */
package com.azure.android.communication.ui.calling.presentation.fragment.calling.rtt

import com.azure.android.communication.ui.calling.redux.state.RttMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class RttViewModel {
    private val isDisplayed = MutableStateFlow<Boolean>(false)
    private val content = MutableStateFlow<List<RttMessage>>(emptyList())

    fun isDisplayed(): StateFlow<Boolean> = isDisplayed
    fun getContent(): StateFlow<List<RttMessage>> = content

    fun update(
        content: List<RttMessage>,
        isDisplayed: Boolean
    ) {
        this.content.value = content
        this.isDisplayed.value = isDisplayed
    }

    fun init(
        content: List<RttMessage>,
        isDisplayed: Boolean
    ) {
        this.content.value = content
        this.isDisplayed.value = isDisplayed
    }
}
/* </RTT_POC> */
