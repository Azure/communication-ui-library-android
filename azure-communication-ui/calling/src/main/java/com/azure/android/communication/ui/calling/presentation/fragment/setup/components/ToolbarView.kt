package com.azure.android.communication.ui.calling.presentation.fragment.setup.components

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.azure.android.communication.ui.calling.implementation.R
import com.azure.android.communication.ui.calling.presentation.DependencyInjectionContainerHolder

internal class ToolbarView : Toolbar {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var diContainer: DependencyInjectionContainerHolder
    private lateinit var navigationButton: ImageButton
    private lateinit var toolbarTitle: TextView
    private lateinit var toolbarSubtitle: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        navigationButton = findViewById(R.id.navigation_button);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarSubtitle = findViewById(R.id.toolbar_subtitle);
    }

    fun start(holder: DependencyInjectionContainerHolder, callCompositeActivity: AppCompatActivity) {
        this.diContainer = holder
        setActionBarTitleSubtitle()

        navigationButton.setOnClickListener {
            callCompositeActivity?.finish();
        }
    }

    fun stop() {
        rootView.invalidate()
        // to fix memory leak
    }

    private fun setActionBarTitleSubtitle() {

        val localOptions = diContainer.container.configuration.callCompositeLocalOptions
        val titleText = if (!TextUtils.isEmpty(localOptions?.setupScreenViewData?.title)) {
            localOptions?.setupScreenViewData?.title
        } else {
            context.applicationContext.getString(R.string.azure_communication_ui_calling_call_setup_action_bar_title)
        }

        toolbarTitle.text = titleText
        toolbarTitle.contentDescription = titleText + context.applicationContext.getString(R.string.azure_communication_ui_calling_call_setup_toolbar_title_announcement)

        // Only set the subtitle if the title has also been set
        if (!TextUtils.isEmpty(localOptions?.setupScreenViewData?.subtitle)) {
            if (!TextUtils.isEmpty(localOptions?.setupScreenViewData?.title)) {
                val subtitleText = localOptions?.setupScreenViewData?.subtitle
                toolbarSubtitle.visibility = View.VISIBLE
                toolbarSubtitle.text = subtitleText
                toolbarSubtitle.contentDescription = subtitleText + context.applicationContext.getString(R.string.azure_communication_ui_calling_call_setup_toolbar_subtitle_announcement)
            } else {
                diContainer.container.logger.error(
                    "Provided setupScreenViewData has subtitle, but no title provided. In this case subtitle is not displayed."
                )
            }
        }
    }
}