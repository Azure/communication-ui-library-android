package com.azure.android.communication.ui.chat;
/**
 * {@link ChatCompositeEventsHandler}&lt;T&gt;
 *
 * <p>A generic handler for call composite events.</p>
 * <p> - {@link ChatComposite#addOnErrorEventHandler(ChatCompositeEventsHandler)} for Error Handling</p>
 * for Remote Participant Join Notifications</p>
 *
 * @param <T> The callback event Type.
 */
public interface ChatCompositeEventHandler <T> {
    /**
     * A callback method to process error event of type T
     *
     * @param eventArgs {@link T}
     */
    void handle(T eventArgs);
}
