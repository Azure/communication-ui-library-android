package com.azure.android.communication.ui.presentation.fragment.setup.components

import android.graphics.Bitmap
import android.widget.ImageView
import com.azure.android.communication.ui.configuration.LocalParticipantConfiguration
import com.azure.android.communication.ui.helper.MainCoroutineRule
import com.azure.android.communication.ui.persona.PersonaData
import com.azure.android.communication.ui.presentation.fragment.factories.SetupViewModelFactory
import com.azure.android.communication.ui.presentation.manager.PersonaManager
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
        val personaData = PersonaData("test")

        val localParticipantConfiguration = LocalParticipantConfiguration(personaData)
        val personaManager = PersonaManager(localParticipantConfiguration)

        val mockAppStore = mock<AppStore<ReduxState>> {}
        val setupViewModelFactory =
            SetupViewModelFactory(mockAppStore, personaManager)

        // act
        val viewModel = setupViewModelFactory.provideParticipantAvatarViewModel()

        // assert
        Assert.assertEquals(
            personaData.name,
            viewModel.getPersonaData()?.name
        )

        Assert.assertEquals(
            null,
            viewModel.getPersonaData()?.image
        )
    }

    @Test
    fun setupParticipantAvatarViewModel_getPersonaData_onCall_returnsPersonaImage_when_personaImageIsSet() {
        // arrange
        val mockBitmap = mock<Bitmap> {}
        val personaData = PersonaData(mockBitmap)

        val localParticipantConfiguration = LocalParticipantConfiguration(personaData)
        val personaManager = PersonaManager(localParticipantConfiguration)

        val mockAppStore = mock<AppStore<ReduxState>> {}
        val setupViewModelFactory =
            SetupViewModelFactory(mockAppStore, personaManager)

        // act
        val viewModel = setupViewModelFactory.provideParticipantAvatarViewModel()

        // assert
        Assert.assertEquals(
            null,
            viewModel.getPersonaData()?.name
        )

        Assert.assertEquals(
            mockBitmap,
            viewModel.getPersonaData()?.image
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
        val personaData = PersonaData("hello", mockBitmap, ImageView.ScaleType.CENTER)

        val localParticipantConfiguration = LocalParticipantConfiguration(personaData)
        val personaManager = PersonaManager(localParticipantConfiguration)

        val mockAppStore = mock<AppStore<ReduxState>> {}
        val setupViewModelFactory =
            SetupViewModelFactory(mockAppStore, personaManager)

        // act
        val viewModel = setupViewModelFactory.provideParticipantAvatarViewModel()

        // assert
        Assert.assertEquals(
            "hello",
            viewModel.getPersonaData()?.name
        )

        Assert.assertEquals(
            mockBitmap,
            viewModel.getPersonaData()?.image
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
        val personaData = PersonaData(mockBitmap, ImageView.ScaleType.CENTER)

        val localParticipantConfiguration = LocalParticipantConfiguration(personaData)
        val personaManager = PersonaManager(localParticipantConfiguration)

        val mockAppStore = mock<AppStore<ReduxState>> {}
        val setupViewModelFactory =
            SetupViewModelFactory(mockAppStore, personaManager)

        // act
        val viewModel = setupViewModelFactory.provideParticipantAvatarViewModel()

        // assert
        Assert.assertEquals(
            null,
            viewModel.getPersonaData()?.name
        )

        Assert.assertEquals(
            mockBitmap,
            viewModel.getPersonaData()?.image
        )

        Assert.assertEquals(
            ImageView.ScaleType.CENTER,
            viewModel.getPersonaData()?.scaleType
        )
    }
}
