// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.service.sdk

import android.view.View

internal class VideoStreamRendererViewWrapper(private val view: com.azure.android.communication.calling.VideoStreamRendererView) :
    VideoStreamRendererView {
    override fun dispose() {
        view.dispose()
    }

    override fun getView(): View? = view
}
