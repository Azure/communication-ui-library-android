package com.azure.android.communication.ui.calling.service;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.azure.android.communication.ui.calling.redux.Store;
import com.azure.android.communication.ui.calling.redux.action.AudioDeviceAction;
import com.azure.android.communication.ui.calling.redux.state.AudioDevice;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class AudioDeviceServiceTest {
    @Mock
    private Context mockContext;
    @Mock
    private AudioManager mockAudioManager;
    @Mock
    private Store mockStore;

    private AudioDeviceService audioDeviceService;
    private ArgumentCaptor<Object> actionCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);
        audioDeviceService = new AudioDeviceService(mockContext, mockStore);
        actionCaptor = ArgumentCaptor.forClass(Object.class);
    }

    @Test
    public void testStart_RegistersReceiver() {
        audioDeviceService.start();
        verify(mockContext).registerReceiver(any(), any());
    }

    @Test
    public void testStop_UnregistersReceiver() {
        audioDeviceService.stop();
        verify(mockContext).unregisterReceiver(any());
    }

    @Test
    public void testSelectDevice_Speaker() {
        AudioDevice speaker = new AudioDevice(AudioDeviceType.SPEAKER, "speaker", "Speaker");
        audioDeviceService.selectDevice(speaker);

        verify(mockAudioManager).setMode(AudioManager.MODE_NORMAL);
        verify(mockAudioManager).setSpeakerphoneOn(true);
        verify(mockAudioManager).setBluetoothScoOn(false);
        verify(mockStore).dispatch(any(AudioDeviceAction.DeviceSelected.class));
    }

    @Test
    public void testSelectDevice_Bluetooth() {
        AudioDevice bluetooth = new AudioDevice(AudioDeviceType.BLUETOOTH, "bluetooth", "Bluetooth");
        audioDeviceService.selectDevice(bluetooth);

        verify(mockAudioManager).setMode(AudioManager.MODE_NORMAL);
        verify(mockAudioManager).setSpeakerphoneOn(false);
        verify(mockAudioManager).setBluetoothScoOn(true);
        verify(mockAudioManager).startBluetoothSco();
        verify(mockStore).dispatch(any(AudioDeviceAction.DeviceSelected.class));
    }

    @Test
    public void testSelectDevice_WiredHeadset() {
        AudioDevice wiredHeadset = new AudioDevice(AudioDeviceType.WIRED_HEADSET, "wired_headset", "Wired Headset");
        audioDeviceService.selectDevice(wiredHeadset);

        verify(mockAudioManager).setMode(AudioManager.MODE_NORMAL);
        verify(mockAudioManager).setSpeakerphoneOn(false);
        verify(mockAudioManager).setBluetoothScoOn(false);
        verify(mockStore).dispatch(any(AudioDeviceAction.DeviceSelected.class));
    }

    @Test
    public void testHandleWiredHeadsetConnection_Connected() {
        Intent intent = new Intent(AudioManager.ACTION_HEADSET_PLUG);
        intent.putExtra("state", 1);
        audioDeviceService.start();

        // Simulate broadcast receiver
        ArgumentCaptor<android.content.BroadcastReceiver> receiverCaptor = 
            ArgumentCaptor.forClass(android.content.BroadcastReceiver.class);
        verify(mockContext).registerReceiver(receiverCaptor.capture(), any());
        receiverCaptor.getValue().onReceive(mockContext, intent);

        // Verify actions dispatched
        verify(mockStore, times(3)).dispatch(actionCaptor.capture());
        
        // First action should be WiredHeadsetStateChanged
        assertTrue(actionCaptor.getAllValues().get(0) instanceof AudioDeviceAction.WiredHeadsetStateChanged);
        AudioDeviceAction.WiredHeadsetStateChanged stateAction = 
            (AudioDeviceAction.WiredHeadsetStateChanged) actionCaptor.getAllValues().get(0);
        assertTrue(stateAction.isConnected());

        // Second action should be DeviceConnected
        assertTrue(actionCaptor.getAllValues().get(1) instanceof AudioDeviceAction.DeviceConnected);

        // Third action should be DevicesDiscovered
        assertTrue(actionCaptor.getAllValues().get(2) instanceof AudioDeviceAction.DevicesDiscovered);
    }

    @Test
    public void testHandleBluetoothScoStateChange_Connected() {
        Intent intent = new Intent(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
        intent.putExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, AudioManager.SCO_AUDIO_STATE_CONNECTED);
        audioDeviceService.start();

        // Simulate broadcast receiver
        ArgumentCaptor<android.content.BroadcastReceiver> receiverCaptor = 
            ArgumentCaptor.forClass(android.content.BroadcastReceiver.class);
        verify(mockContext).registerReceiver(receiverCaptor.capture(), any());
        receiverCaptor.getValue().onReceive(mockContext, intent);

        // Verify actions dispatched
        verify(mockStore, times(3)).dispatch(actionCaptor.capture());
        
        // First action should be BluetoothStateChanged
        assertTrue(actionCaptor.getAllValues().get(0) instanceof AudioDeviceAction.BluetoothStateChanged);
        AudioDeviceAction.BluetoothStateChanged stateAction = 
            (AudioDeviceAction.BluetoothStateChanged) actionCaptor.getAllValues().get(0);
        assertTrue(stateAction.isConnected());

        // Second action should be DeviceConnected
        assertTrue(actionCaptor.getAllValues().get(1) instanceof AudioDeviceAction.DeviceConnected);

        // Third action should be DevicesDiscovered
        assertTrue(actionCaptor.getAllValues().get(2) instanceof AudioDeviceAction.DevicesDiscovered);
    }

    @Test
    public void testHandleAudioBecomingNoisy() {
        Intent intent = new Intent(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        audioDeviceService.start();

        // Simulate broadcast receiver
        ArgumentCaptor<android.content.BroadcastReceiver> receiverCaptor = 
            ArgumentCaptor.forClass(android.content.BroadcastReceiver.class);
        verify(mockContext).registerReceiver(receiverCaptor.capture(), any());
        receiverCaptor.getValue().onReceive(mockContext, intent);

        // Verify speaker mode is set
        verify(mockAudioManager).setMode(AudioManager.MODE_NORMAL);
        verify(mockAudioManager).setSpeakerphoneOn(true);
        verify(mockAudioManager).setBluetoothScoOn(false);
        verify(mockStore).dispatch(any(AudioDeviceAction.DeviceSelected.class));
    }

    private static boolean assertTrue(boolean condition) {
        org.junit.Assert.assertTrue(condition);
        return condition;
    }
}
