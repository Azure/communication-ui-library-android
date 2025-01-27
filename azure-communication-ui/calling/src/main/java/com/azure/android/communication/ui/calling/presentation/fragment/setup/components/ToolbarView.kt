// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions

internal class ToolbarView : Toolbar {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var navigationButton: ImageButton
    private lateinit var toolbarTitle: TextView
    private lateinit var toolbarSubtitle: TextView
    private lateinit var logger: Logger
    private var callCompositeLocalOptions: CallCompositeLocalOptions? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        navigationButton = findViewById(R.id.azure_communication_ui_navigation_button)
        toolbarTitle = findViewById(R.id.azure_communication_ui_toolbar_title)
        toolbarSubtitle = findViewById(R.id.azure_communication_ui_toolbar_subtitle)
        this.isFocusable = true
        this.isFocusableInTouchMode = true
    }

    fun start(
        callCompositeLocalOptions: CallCompositeLocalOptions?,
        logger: Logger,
        exitComposite: () -> Unit
    ) {
        this.callCompositeLocalOptions = callCompositeLocalOptions
        this.logger = logger
        setActionBarTitleSubtitle()
        navigationButton.setOnClickListener {
            exitComposite()
        }
        navigationButton.requestFocus()
    }

    fun stop() {
        // to fix memory leak
        rootView.invalidate()
    }

    private fun setActionBarTitleSubtitle() {
        val localOptions = callCompositeLocalOptions
        val titleText = if (!TextUtils.isEmpty(localOptions?.setupScreenViewData?.title)) {
            localOptions?.setupScreenViewData?.title
        } else {
            context.getString(R.string.azure_communication_ui_calling_call_setup_action_bar_title)
        }

        toolbarTitle.text = titleText

        // Only set the subtitle if the title has also been set
        if (!TextUtils.isEmpty(localOptions?.setupScreenViewData?.subtitle)) {
            if (!TextUtils.isEmpty(localOptions?.setupScreenViewData?.title)) {
                val subtitleText = localOptions?.setupScreenViewData?.subtitle
                toolbarSubtitle.visibility = View.VISIBLE
                toolbarSubtitle.text = subtitleText
                toolbarSubtitle.contentDescription = subtitleText + " " + context.getString(R.string.azure_communication_ui_calling_call_setup_toolbar_subtitle_announcement)
            } else {
                logger.error(
                    "Provided setupScreenViewData has subtitle, but no title provided. In this case subtitle is not displayed."
                )
            }
        }
    }
}
