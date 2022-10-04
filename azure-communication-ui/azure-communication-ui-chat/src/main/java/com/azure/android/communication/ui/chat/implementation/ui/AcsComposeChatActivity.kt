package com.azure.android.communication.ui.chat.implementation.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.azure.android.communication.ui.arch.locator.ServiceLocator
import com.azure.android.communication.ui.chat.implementation.ui.view.LiveStateComposeChatView
import java.util.Locale

class AcsComposeChatActivity : AppCompatActivity(), StopNotification {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config: Configuration = resources.configuration
        config.setLocale(intent.getStringExtra(KEY_LOCALE)?.let { Locale(it) })
        resources.updateConfiguration(config, resources.displayMetrics)
        setContentView(LiveStateComposeChatView(this, intent.getIntExtra(KEY_INSTANCE_ID, 0)))
        val locator = ServiceLocator.getInstance(intent.getIntExtra(KEY_INSTANCE_ID, 0))

        locator.locate<UINotifier>().registerForStop(this)
    }

    override fun stop() {
        this.finish()
    }

    companion object {
        const val KEY_INSTANCE_ID = "instanceId"
        const val KEY_LOCALE = "locale"
    }
}
