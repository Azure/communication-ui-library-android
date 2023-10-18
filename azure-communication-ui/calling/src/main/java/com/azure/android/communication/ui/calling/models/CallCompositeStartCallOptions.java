package com.azure.android.communication.ui.calling.models;

import java.util.List;

public class CallCompositeStartCallOptions {
    private final String displayName;
    private final List<String> participants;

    public CallCompositeStartCallOptions(String displayName, List<String> participants) {
        this.displayName = displayName;
        this.participants = participants;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getParticipants() {
        return participants;
    }
}
