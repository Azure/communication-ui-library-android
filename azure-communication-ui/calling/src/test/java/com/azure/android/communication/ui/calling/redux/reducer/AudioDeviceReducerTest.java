package com.azure.android.communication.ui.calling.redux.reducer;

import com.azure.android.communication.ui.calling.redux.action.AudioDeviceAction;
import com.azure.android.communication.ui.calling.redux.state.AudioDevice;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceState;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceType;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSwitchingState;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AudioDeviceReducerTest {
    private final AudioDevice speaker = new AudioDevice(AudioDeviceType.SPEAKER, "speaker", "Speaker");
    private final AudioDevice wiredHeadset = new AudioDevice(AudioDeviceType.WIRED_HEADSET, "wired_headset", "Wired Headset");
    private final AudioDevice bluetooth = new AudioDevice(AudioDeviceType.BLUETOOTH, "bluetooth", "Bluetooth");

    @Test
    public void testInitialState() {
        AudioDeviceState state = AudioDeviceState.getInitialState();
        assertEquals(AudioDeviceType.SPEAKER, state.getCurrentDevice().getType());
        assertEquals(1, state.getAvailableDevices().size());
        assertFalse(state.isWiredHeadsetConnected());
        assertFalse(state.isBluetoothConnected());
        assertEquals(AudioDeviceSwitchingState.NONE, state.getSwitchingState());
        assertNull(state.getError());
    }

    @Test
    public void testDeviceSwitchStarted() {
        AudioDeviceState initialState = createTestState(speaker, false, false);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState,
            new AudioDeviceAction.DeviceSwitchStarted(wiredHeadset, speaker));

        assertEquals(AudioDeviceSwitchingState.SWITCHING, newState.getSwitchingState());
        assertEquals(speaker, newState.getCurrentDevice());
        assertNull(newState.getError());
    }

    @Test
    public void testDeviceSwitchCompleted() {
        AudioDeviceState initialState = createTestState(speaker, false, false)
            .withSwitchingState(AudioDeviceSwitchingState.SWITCHING);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState,
            new AudioDeviceAction.DeviceSwitchCompleted(wiredHeadset));

        assertEquals(AudioDeviceSwitchingState.NONE, newState.getSwitchingState());
        assertEquals(wiredHeadset, newState.getCurrentDevice());
        assertNull(newState.getError());
    }

    @Test
    public void testDeviceSwitchFailed() {
        AudioDeviceState initialState = createTestState(speaker, false, false)
            .withSwitchingState(AudioDeviceSwitchingState.SWITCHING);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState,
            new AudioDeviceAction.DeviceSwitchFailed(wiredHeadset, speaker, "Test error"));

        assertEquals(AudioDeviceSwitchingState.NONE, newState.getSwitchingState());
        assertEquals(speaker, newState.getCurrentDevice());
        assertEquals("Test error", newState.getError());
    }

    @Test
    public void testDeviceConnected_DuringSwitching_ShouldNotAutoSwitch() {
        AudioDeviceState initialState = createTestState(speaker, false, false)
            .withSwitchingState(AudioDeviceSwitchingState.SWITCHING);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState,
            new AudioDeviceAction.DeviceConnected(wiredHeadset));

        assertEquals(AudioDeviceSwitchingState.SWITCHING, newState.getSwitchingState());
        assertEquals(speaker, newState.getCurrentDevice());
        assertTrue(newState.getAvailableDevices().contains(wiredHeadset));
    }

    @Test
    public void testDeviceDisconnected_DuringSwitching_ShouldNotSwitch() {
        List<AudioDevice> devices = Arrays.asList(speaker, wiredHeadset, bluetooth);
        AudioDeviceState initialState = new AudioDeviceState(speaker, devices, true, true,
            AudioDeviceSwitchingState.SWITCHING, null);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState,
            new AudioDeviceAction.DeviceDisconnected(bluetooth));

        assertEquals(AudioDeviceSwitchingState.SWITCHING, newState.getSwitchingState());
        assertEquals(speaker, newState.getCurrentDevice());
        assertFalse(newState.getAvailableDevices().contains(bluetooth));
    }

    @Test
    public void testDevicesDiscovered_DuringSwitching_ShouldNotUpdateCurrent() {
        List<AudioDevice> initialDevices = Arrays.asList(speaker, wiredHeadset, bluetooth);
        AudioDeviceState initialState = new AudioDeviceState(speaker, initialDevices, true, true,
            AudioDeviceSwitchingState.SWITCHING, null);
        
        List<AudioDevice> newDevices = Arrays.asList(bluetooth, speaker);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState,
            new AudioDeviceAction.DevicesDiscovered(newDevices));

        assertEquals(AudioDeviceSwitchingState.SWITCHING, newState.getSwitchingState());
        assertEquals(speaker, newState.getCurrentDevice());
        assertEquals(newDevices, newState.getAvailableDevices());
    }

    @Test
    public void testAudioBecomingNoisy_DuringSwitching_ShouldNotSwitch() {
        AudioDeviceState initialState = createTestState(wiredHeadset, true, false)
            .withSwitchingState(AudioDeviceSwitchingState.SWITCHING);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState,
            new AudioDeviceAction.AudioBecomingNoisy(speaker));

        assertEquals(AudioDeviceSwitchingState.SWITCHING, newState.getSwitchingState());
        assertEquals(wiredHeadset, newState.getCurrentDevice());
    }

    private AudioDeviceState createTestState(AudioDevice currentDevice, boolean wiredConnected, boolean bluetoothConnected) {
        List<AudioDevice> devices = new ArrayList<>();
        devices.add(speaker);
        if (wiredConnected) devices.add(wiredHeadset);
        if (bluetoothConnected) devices.add(bluetooth);
        
        return new AudioDeviceState(currentDevice, devices, wiredConnected, bluetoothConnected,
            AudioDeviceSwitchingState.NONE, null);
    }

    // Original tests remain unchanged below
    @Test
    public void testDeviceConnected_HigherPriority_ShouldAutoSwitch() {
        AudioDeviceState initialState = createTestState(speaker, false, false);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState,
            new AudioDeviceAction.DeviceConnected(wiredHeadset));

        assertEquals(wiredHeadset, newState.getCurrentDevice());
        assertTrue(newState.getAvailableDevices().contains(wiredHeadset));
        assertTrue(newState.getAvailableDevices().contains(speaker));
    }

    @Test
    public void testDeviceConnected_LowerPriority_ShouldNotAutoSwitch() {
        AudioDeviceState initialState = createTestState(wiredHeadset, true, false);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState,
            new AudioDeviceAction.DeviceConnected(bluetooth));

        assertEquals(wiredHeadset, newState.getCurrentDevice());
        assertTrue(newState.getAvailableDevices().contains(bluetooth));
        assertTrue(newState.getAvailableDevices().contains(wiredHeadset));
    }

    @Test
    public void testDeviceDisconnected_CurrentDevice_ShouldSwitchToNextHighestPriority() {
        AudioDeviceState initialState = createTestState(wiredHeadset, true, true);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState,
            new AudioDeviceAction.DeviceDisconnected(wiredHeadset));

        assertEquals(bluetooth, newState.getCurrentDevice());
        assertFalse(newState.getAvailableDevices().contains(wiredHeadset));
        assertTrue(newState.getAvailableDevices().contains(bluetooth));
        assertTrue(newState.getAvailableDevices().contains(speaker));
    }

    @Test
    public void testDeviceDisconnected_NotCurrentDevice_ShouldNotSwitch() {
        AudioDeviceState initialState = createTestState(wiredHeadset, true, true);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState,
            new AudioDeviceAction.DeviceDisconnected(bluetooth));

        assertEquals(wiredHeadset, newState.getCurrentDevice());
        assertTrue(newState.getAvailableDevices().contains(wiredHeadset));
        assertFalse(newState.getAvailableDevices().contains(bluetooth));
        assertTrue(newState.getAvailableDevices().contains(speaker));
    }

    @Test
    public void testWiredHeadsetStateChanged() {
        AudioDeviceState initialState = AudioDeviceState.getInitialState();
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState,
            new AudioDeviceAction.WiredHeadsetStateChanged(true));

        assertTrue(newState.isWiredHeadsetConnected());
    }

    @Test
    public void testBluetoothStateChanged() {
        AudioDeviceState initialState = AudioDeviceState.getInitialState();
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState,
            new AudioDeviceAction.BluetoothStateChanged(true));

        assertTrue(newState.isBluetoothConnected());
    }
}
