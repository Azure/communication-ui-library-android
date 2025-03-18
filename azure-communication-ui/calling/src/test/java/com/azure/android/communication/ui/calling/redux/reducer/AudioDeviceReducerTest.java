package com.azure.android.communication.ui.calling.redux.reducer;

import com.azure.android.communication.ui.calling.redux.action.AudioDeviceAction;
import com.azure.android.communication.ui.calling.redux.state.AudioDevice;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceState;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceType;

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
    }

    @Test
    public void testDeviceConnected_HigherPriority_ShouldAutoSwitch() {
        AudioDeviceState initialState = new AudioDeviceState(speaker, Arrays.asList(speaker), false, false);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState, new AudioDeviceAction.DeviceConnected(wiredHeadset));

        assertEquals(wiredHeadset, newState.getCurrentDevice());
        assertTrue(newState.getAvailableDevices().contains(wiredHeadset));
        assertTrue(newState.getAvailableDevices().contains(speaker));
    }

    @Test
    public void testDeviceConnected_LowerPriority_ShouldNotAutoSwitch() {
        AudioDeviceState initialState = new AudioDeviceState(wiredHeadset, Arrays.asList(wiredHeadset), true, false);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState, new AudioDeviceAction.DeviceConnected(bluetooth));

        assertEquals(wiredHeadset, newState.getCurrentDevice());
        assertTrue(newState.getAvailableDevices().contains(bluetooth));
        assertTrue(newState.getAvailableDevices().contains(wiredHeadset));
    }

    @Test
    public void testDeviceDisconnected_CurrentDevice_ShouldSwitchToNextHighestPriority() {
        List<AudioDevice> devices = new ArrayList<>(Arrays.asList(wiredHeadset, bluetooth, speaker));
        AudioDeviceState initialState = new AudioDeviceState(wiredHeadset, devices, true, true);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState, new AudioDeviceAction.DeviceDisconnected(wiredHeadset));

        assertEquals(bluetooth, newState.getCurrentDevice());
        assertFalse(newState.getAvailableDevices().contains(wiredHeadset));
        assertTrue(newState.getAvailableDevices().contains(bluetooth));
        assertTrue(newState.getAvailableDevices().contains(speaker));
    }

    @Test
    public void testDeviceDisconnected_NotCurrentDevice_ShouldNotSwitch() {
        List<AudioDevice> devices = new ArrayList<>(Arrays.asList(wiredHeadset, bluetooth, speaker));
        AudioDeviceState initialState = new AudioDeviceState(wiredHeadset, devices, true, true);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState, new AudioDeviceAction.DeviceDisconnected(bluetooth));

        assertEquals(wiredHeadset, newState.getCurrentDevice());
        assertTrue(newState.getAvailableDevices().contains(wiredHeadset));
        assertFalse(newState.getAvailableDevices().contains(bluetooth));
        assertTrue(newState.getAvailableDevices().contains(speaker));
    }

    @Test
    public void testDeviceSelected_ValidDevice_ShouldSwitch() {
        List<AudioDevice> devices = new ArrayList<>(Arrays.asList(wiredHeadset, bluetooth, speaker));
        AudioDeviceState initialState = new AudioDeviceState(wiredHeadset, devices, true, true);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState, new AudioDeviceAction.DeviceSelected(bluetooth));

        assertEquals(bluetooth, newState.getCurrentDevice());
    }

    @Test
    public void testDeviceSelected_InvalidDevice_ShouldNotSwitch() {
        List<AudioDevice> devices = new ArrayList<>(Arrays.asList(wiredHeadset, speaker));
        AudioDeviceState initialState = new AudioDeviceState(wiredHeadset, devices, true, false);
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState, new AudioDeviceAction.DeviceSelected(bluetooth));

        assertEquals(wiredHeadset, newState.getCurrentDevice());
    }

    @Test
    public void testDevicesDiscovered_CurrentDeviceNotAvailable_ShouldSwitchToHighestPriority() {
        List<AudioDevice> initialDevices = new ArrayList<>(Arrays.asList(wiredHeadset, bluetooth, speaker));
        AudioDeviceState initialState = new AudioDeviceState(wiredHeadset, initialDevices, true, true);
        
        List<AudioDevice> newDevices = new ArrayList<>(Arrays.asList(bluetooth, speaker));
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState, new AudioDeviceAction.DevicesDiscovered(newDevices));

        assertEquals(bluetooth, newState.getCurrentDevice());
        assertEquals(newDevices, newState.getAvailableDevices());
    }

    @Test
    public void testDevicesDiscovered_CurrentDeviceAvailable_ShouldKeepCurrentDevice() {
        List<AudioDevice> initialDevices = new ArrayList<>(Arrays.asList(wiredHeadset, bluetooth, speaker));
        AudioDeviceState initialState = new AudioDeviceState(wiredHeadset, initialDevices, true, true);
        
        List<AudioDevice> newDevices = new ArrayList<>(Arrays.asList(wiredHeadset, speaker));
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState, new AudioDeviceAction.DevicesDiscovered(newDevices));

        assertEquals(wiredHeadset, newState.getCurrentDevice());
        assertEquals(newDevices, newState.getAvailableDevices());
    }

    @Test
    public void testWiredHeadsetStateChanged() {
        AudioDeviceState initialState = AudioDeviceState.getInitialState();
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState, new AudioDeviceAction.WiredHeadsetStateChanged(true));

        assertTrue(newState.isWiredHeadsetConnected());
    }

    @Test
    public void testBluetoothStateChanged() {
        AudioDeviceState initialState = AudioDeviceState.getInitialState();
        AudioDeviceState newState = AudioDeviceReducer.reduce(initialState, new AudioDeviceAction.BluetoothStateChanged(true));

        assertTrue(newState.isBluetoothConnected());
    }
}
