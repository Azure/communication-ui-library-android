package com.azure.android.communication.ui.presentation.fragment.setup.components

import android.graphics.Bitmap
import android.widget.ImageView
import com.azure.android.communication.ui.configuration.CommunicationUILocalDataOptions
import com.azure.android.communication.ui.configuration.RemoteParticipantsConfiguration
import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.persona.CommunicationUIPersonaData
import com.azure.android.communication.ui.presentation.fragment.factories.SetupViewModelFactory
import com.azure.android.communication.ui.presentation.manager.AvatarViewManager
import com.azure.android.communication.ui.redux.AppStore
import com.azure.android.communication.ui.redux.state.PermissionState
import com.azure.android.communication.ui.redux.state.PermissionStatus
import com.azure.android.communication.ui.redux.state.ReduxState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
internal class SetupParticipantAvatarViewModelUnitTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Test
    fun setupParticipantAvatarViewModel_onUpdate_notifyDisplayAvatarTrue_when_videoStreamIDHasNoValueAndHasCameraPermissions() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val viewModel = SetupParticipantAvatarViewModel(null)
            viewModel.init(
                "",
                "",
                PermissionState(PermissionStatus.DENIED, PermissionStatus.DENIED)
            )

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getShouldDisplayAvatarViewStateFlow()
                    .toList(emitResult)
            }

            // act
            viewModel.update(
                "",
                PermissionState(PermissionStatus.GRANTED, PermissionStatus.GRANTED)
            )

            // assert
            Assert.assertEquals(
                false,
                emitResult[0]
            )

            Assert.assertEquals(
                true,
                emitResult[1]
            )

            resultFlow.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun setupParticipantAvatarViewModel_onUpdate_notifyDisplayAvatarFalse_when_videoStreamIDHasValueAndHasCameraPermissions() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val viewModel = SetupParticipantAvatarViewModel(null)
            viewModel.init(
                "",
                "",
                PermissionState(PermissionStatus.GRANTED, PermissionStatus.GRANTED)
            )

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getShouldDisplayAvatarViewStateFlow()
                    .toList(emitResult)
            }

            // act
            viewModel.update(
                "id",
                PermissionState(PermissionStatus.GRANTED, PermissionStatus.GRANTED)
            )

            // assert
            Assert.assertEquals(
                true,
                emitResult[0]
            )

            Assert.assertEquals(
                false,
                emitResult[1]
            )

            resultFlow.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun setupParticipantAvatarViewModel_onUpdate_notifyDisplayAvatarFalse_when_videoStreamIDHasNoValueAndHasCameraPermissionsDenied() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val viewModel = SetupParticipantAvatarViewModel(null)
            viewModel.init(
                "",
                "",
                PermissionState(PermissionStatus.GRANTED, PermissionStatus.GRANTED)
            )

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getShouldDisplayAvatarViewStateFlow()
                    .toList(emitResult)
            }

            // act
            viewModel.update("", PermissionState(PermissionStatus.GRANTED, PermissionStatus.DENIED))

            // assert
            Assert.assertEquals(
                true,
                emitResult[0]
            )

            Assert.assertEquals(
                false,
                emitResult[1]
            )

            resultFlow.cancel()
        }

    @ExperimentalCoroutinesApi
    @Test
    fun setupParticipantAvatarViewModel_onUpdate_notifyDisplayAvatarFalse_when_videoStreamIDHasNoValueAndHasAudioPermissionsDenied() =
        mainCoroutineRule.testDispatcher.runBlockingTest {
            // arrange
            val viewModel = SetupParticipantAvatarViewModel(null)
            viewModel.init(
                "",
                "",
                PermissionState(PermissionStatus.GRANTED, PermissionStatus.GRANTED)
            )

            val emitResult = mutableListOf<Boolean>()

            val resultFlow = launch {
                viewModel.getShouldDisplayAvatarViewStateFlow()
                    .toList(emitResult)
            }

            // act
            viewModel.update("", PermissionState(PermissionStatus.DENIED, PermissionStatus.GRANTED))

            // assert
            Assert.assertEquals(
                true,
                emitResult[0]
            )

            Assert.assertEquals(
                false,
                emitResult[1]
            )

            resultFlow.cancel()
        }

    @Test
    fun setupParticipantAvatarViewModel_getPersonaData_onCall_returnsPersonaName_when_personaDataNameIsSet() {
        // arrange
        val personaData =
            CommunicationUIPersonaData(
                "test"
            )

        val dataOptions =
            CommunicationUILocalDataOptions(
                personaData
            )
        val mockAppStore = mock<AppStore<ReduxState>> {}
        val avatarViewManager = AvatarViewManager(mockAppStore, dataOptions, RemoteParticipantsConfiguration())

        val setupViewModelFactory =
            SetupViewModelFactory(mockAppStore, avatarViewManager)

        // act
        val viewModel = setupViewModelFactory.provideParticipantAvatarViewModel()

        // assert
        Assert.assertEquals(
            personaData.renderedDisplayName,
            viewModel.getPersonaData()?.renderedDisplayName
        )

        Assert.assertEquals(
            null,
            viewModel.getPersonaData()?.avatarBitmap
        )
    }

    @Test
    fun setupParticipantAvatarViewModel_getPersonaData_onCall_returnsPersonaImage_when_personaImageIsSet() {
        // arrange
        val mockBitmap = mock<Bitmap> {}
        val personaData =
            CommunicationUIPersonaData(
                mockBitmap
            )

        val dataOptions =
            CommunicationUILocalDataOptions(
                personaData
            )
        val mockAppStore = mock<AppStore<ReduxState>> {}
        val avatarViewManager = AvatarViewManager(mockAppStore, dataOptions, RemoteParticipantsConfiguration())

        val setupViewModelFactory =
            SetupViewModelFactory(mockAppStore, avatarViewManager)

        // act
        val viewModel = setupViewModelFactory.provideParticipantAvatarViewModel()

        // assert
        Assert.assertEquals(
            null,
            viewModel.getPersonaData()?.renderedDisplayName
        )

        Assert.assertEquals(
            mockBitmap,
            viewModel.getPersonaData()?.avatarBitmap
        )

        Assert.assertEquals(
            ImageView.ScaleType.FIT_XY,
            viewModel.getPersonaData()?.scaleType
        )
    }

    @Test
    fun setupParticipantAvatarViewModel_getPersonaData_onCall_returnsPersonaData_when_personaDataIsSet() {
        // arrange
        val mockBitmap = mock<Bitmap> {}
        val personaData =
            CommunicationUIPersonaData(
                "hello",
                mockBitmap,
                ImageView.ScaleType.CENTER
            )

        val dataOptions =
            CommunicationUILocalDataOptions(
                personaData
            )
        val mockAppStore = mock<AppStore<ReduxState>> {}
        val avatarViewManager = AvatarViewManager(mockAppStore, dataOptions, RemoteParticipantsConfiguration())

        val setupViewModelFactory =
            SetupViewModelFactory(mockAppStore, avatarViewManager)

        // act
        val viewModel = setupViewModelFactory.provideParticipantAvatarViewModel()

        // assert
        Assert.assertEquals(
            "hello",
            viewModel.getPersonaData()?.renderedDisplayName
        )

        Assert.assertEquals(
            mockBitmap,
            viewModel.getPersonaData()?.avatarBitmap
        )

        Assert.assertEquals(
            ImageView.ScaleType.CENTER,
            viewModel.getPersonaData()?.scaleType
        )
    }

    @Test
    fun setupParticipantAvatarViewModel_getPersonaScale_onCall_returnsPersonaScale_when_personaScaleIsSet() {
        // arrange
        val mockBitmap = mock<Bitmap> {}
        val personaData =
            CommunicationUIPersonaData(
                mockBitmap,
                ImageView.ScaleType.CENTER
            )

        val dataOptions =
            CommunicationUILocalDataOptions(
                personaData
            )
        val mockAppStore = mock<AppStore<ReduxState>> {}
        val avatarViewManager = AvatarViewManager(mockAppStore, dataOptions, RemoteParticipantsConfiguration())
        val setupViewModelFactory =
            SetupViewModelFactory(mockAppStore, avatarViewManager)

        // act
        val viewModel = setupViewModelFactory.provideParticipantAvatarViewModel()

        // assert
        Assert.assertEquals(
            null,
            viewModel.getPersonaData()?.renderedDisplayName
        )

        Assert.assertEquals(
            mockBitmap,
            viewModel.getPersonaData()?.avatarBitmap
        )

        Assert.assertEquals(
            ImageView.ScaleType.CENTER,
            viewModel.getPersonaData()?.scaleType
        )
    }
}
