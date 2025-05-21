package com.azure.android.communication.ui.calling.redux.middleware;

import android.os.Handler;
import android.os.Looper;

import com.azure.android.communication.ui.calling.redux.action.Action;
import com.azure.android.communication.ui.calling.redux.action.AudioDeviceAction;
import com.azure.android.communication.ui.calling.service.AudioDeviceService;
import com.azure.android.communication.ui.calling.redux.Store;
import com.azure.android.communication.ui.calling.redux.state.AudioDevice;
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceState;
import com.azure.android.communication.ui.calling.redux.state.ReduxState;
import com.azure.android.communication.ui.calling.redux.Dispatch;
import com.azure.android.communication.ui.calling.redux.Middleware;

/**
 * Middleware for handling audio device switching operations.
 */
public final class AudioDeviceMiddleware implements Middleware<ReduxState> {
    private final AudioDeviceService audioDeviceService;
    private final Handler mainHandler;

    public AudioDeviceMiddleware(AudioDeviceService audioDeviceService) {
        this.audioDeviceService = audioDeviceService;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public Dispatch.Fn invoke(final Store<ReduxState> store) {
        return next -> action -> {
            if (action instanceof AudioDeviceAction.SelectDeviceRequested) {
                return handleDeviceSelection((AudioDeviceAction.SelectDeviceRequested) action, store, next);
            }
            
            // Pass through all other actions
            return next.invoke(action);
        };
    }

    private Object handleDeviceSelection(
            AudioDeviceAction.SelectDeviceRequested action,
            Store<ReduxState> store,
            Dispatch next) {
        AudioDevice targetDevice = action.getDevice();
        AudioDeviceState currentState = (AudioDeviceState) store.getCurrentState();
        
        // Verify device is available
        if (!currentState.getAvailableDevices().contains(targetDevice)) {
            return next.invoke(new AudioDeviceAction.DeviceSwitchFailed(
                targetDevice,
                currentState.getCurrentDevice(),
                "Selected device is not available"
            ));
        }

        // Don't switch if already switching
        if (currentState.getSwitchingState() == com.azure.android.communication.ui.calling.redux.state.AudioDeviceSwitchingState.SWITCHING) {
            return null;
        }

        // Don't switch if already using this device
        if (currentState.getCurrentDevice().equals(targetDevice)) {
            return null;
        }

        // Start switching process
        next.invoke(new AudioDeviceAction.DeviceSwitchStarted(
            targetDevice,
            currentState.getCurrentDevice()
        ));

        // Attempt to configure the device asynchronously
        configureDeviceAsync(targetDevice, currentState.getCurrentDevice(), next);

        return null;
    }

    private void configureDeviceAsync(
            final AudioDevice targetDevice,
            final AudioDevice previousDevice,
            final Dispatch next) {
        // Run device configuration on main thread
        mainHandler.post(() -> {
            try {
                boolean success = audioDeviceService.configureAudioRouting(targetDevice);
                
                if (success) {
                    next.invoke(new AudioDeviceAction.DeviceSwitchCompleted(targetDevice));
                } else {
                    next.invoke(new AudioDeviceAction.DeviceSwitchFailed(
                        targetDevice,
                        previousDevice,
                        "Failed to configure audio routing"
                    ));
                    // Attempt to restore previous device
                    audioDeviceService.configureAudioRouting(previousDevice);
                }
            } catch (Exception e) {
                next.invoke(new AudioDeviceAction.DeviceSwitchFailed(
                    targetDevice,
                    previousDevice,
                    "Error configuring audio routing: " + e.getMessage()
                ));
                // Attempt to restore previous device
                audioDeviceService.configureAudioRouting(previousDevice);
            }
        });
    }

}
