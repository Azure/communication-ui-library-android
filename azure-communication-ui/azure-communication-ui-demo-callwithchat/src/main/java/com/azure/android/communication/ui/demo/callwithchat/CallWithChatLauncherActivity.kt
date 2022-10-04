@file:OptIn(ExperimentalMaterial3Api::class)

package com.azure.android.communication.ui.demo.callwithchat

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.ui.callwithchat.CallWithChatCompositeBuilder
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeRemoteOptions
import com.azure.android.communication.ui.callwithchat.models.CallWithChatCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.demo.callwithchat.ui.components.RadioGroupTwoButtonColumn
import com.azure.android.communication.ui.demo.callwithchat.ui.components.RadioGroupTwoButtonRow
import com.azure.android.communication.ui.demo.callwithchat.ui.components.UserInputSquareField
import com.azure.android.communication.ui.demo.callwithchat.ui.theme.AzurecommunicationuiTheme

class CallWithChatLauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AzurecommunicationuiTheme {
                Form("Android")
            }
        }
    }
}

@Composable
private fun Form(name: String) {
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            RadioGroupTwoButtonColumn("Token Function", "ACS Function")
            Spacer(modifier = Modifier.height(16.dp))
            UserInputSquareField("User Name", true) {}
            Spacer(modifier = Modifier.height(16.dp))

            RadioGroupTwoButtonRow("Group Url", "Teams Url")
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = LocalContext.current
                Button({ onButtonClick(context) }) { Text("Start Experience") }
            }
        }
    }
}

private fun onButtonClick(context: Context) {
    val meetingLink = ""

    val endpoint = ""
    val locator = CallWithChatCompositeTeamsMeetingLinkLocator(endpoint, meetingLink)

    val tokenRefreshOptions = CommunicationTokenRefreshOptions({
        ""
    }, true)

    val credential = CommunicationTokenCredential(tokenRefreshOptions)
    val communicationUserId = ""

    val remoteOptions = CallWithChatCompositeRemoteOptions(locator, CommunicationUserIdentifier(communicationUserId), credential)

    val callWithChatComposite = CallWithChatCompositeBuilder().build()
    callWithChatComposite.launch(context, remoteOptions)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AzurecommunicationuiTheme {
        Form("Preview")
    }
}
