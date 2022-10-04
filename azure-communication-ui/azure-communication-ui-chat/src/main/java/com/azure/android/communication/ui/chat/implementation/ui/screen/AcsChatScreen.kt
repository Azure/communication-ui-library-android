// @file:OptIn(ExperimentalMaterial3Api::class)

package com.azure.android.communication.ui.chat.implementation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.azure.android.communication.ui.chat.implementation.redux.states.MockParticipant
import com.azure.android.communication.ui.chat.implementation.ui.component.AcsChatActionBarViewModel
import com.azure.android.communication.ui.chat.implementation.ui.component.AcsChatMessageList
import com.azure.android.communication.ui.chat.implementation.ui.component.BottomBar
import com.azure.android.communication.ui.chat.implementation.ui.isScrolledToEnd
import com.azure.android.communication.ui.chat.implementation.ui.mock.MessageFaker
import com.azure.android.communication.ui.chat.implementation.ui.outOfViewItemCount
import com.azure.android.communication.ui.chat.implementation.ui.theme.AzurecommunicationuiTheme
import com.azure.android.communication.ui.chat.implementation.ui.view_models.MockUiViewModel
import kotlinx.coroutines.launch
import com.azure.android.communication.ui.chat.implementation.ui.component.AcsChatActionBar as AcsChatActionBar1

@Composable
fun AcsChatScreen(viewModel: MockUiViewModel) {
    AzurecommunicationuiTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            val autoScroll = remember { mutableStateOf(true) }
            val basicState = remember { mutableStateOf(0f) }
            val minBound = -100f
            val maxBound = 100f
            val onNewDelta: (Float) -> Float = { delta ->
                val oldState = basicState.value
                val newState = (basicState.value + delta).coerceIn(minBound, maxBound)
                basicState.value = newState
                newState - oldState
            }
            val scrollState = rememberLazyListState()

            val scope = rememberCoroutineScope()

            if (scrollState.isScrollInProgress) {
                autoScroll.value = scrollState.isScrolledToEnd()
            }
            if (scrollState.outOfViewItemCount() > 0 && autoScroll.value) {
                LaunchedEffect(scrollState.outOfViewItemCount()) {
                    scope.launch {
                        scrollState.animateScrollToItem(viewModel.mockMessages.size - 1)
                    }
                }
            }
            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPostScroll(
                        consumed: Offset,
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        val vertical = available.y
                        val weConsumed = onNewDelta(vertical)
                        return Offset(x = 0f, y = weConsumed)
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .nestedScroll(nestedScrollConnection)
                ) {
                    AcsChatActionBar1(
                        onNavIconPressed = {},
                        viewModel = AcsChatActionBarViewModel(
                            participantCount = viewModel.participantCount
                        ) {}
                    )
                    AcsChatMessageList(
                        PaddingValues.Absolute(20.dp),
                        viewModel.mockMessages,
                        scrollState,
                        Modifier
                            .fillMaxHeight()
                            .weight(1f)
                    )

                    BottomBar(viewModel) {
                        scope.launch {
                            scrollState.scrollToItem(0)
                        }
                    }
                }
            }
        }
    }
}

val messagePadding: Dp = 6.dp

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AcsChatScreen(
        viewModel = MockUiViewModel(
            listOf(
                MockParticipant("Local User", isCurrentUser = true),
                MockParticipant("Other User"),
                MockParticipant("Other User 3"),

            ),
            MessageFaker(LocalContext.current).generateMessages(10),
            postMessage = {},
            onUserTyping = {},
        )
    )
}
