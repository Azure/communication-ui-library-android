package com.azure.android.communication.ui.calling.models

import com.azure.android.core.util.ExpandableStringEnum

class CallCompositeLeaveCallConfirmationMode :
    ExpandableStringEnum<CallCompositeLeaveCallConfirmationMode>() {

    companion object {
        /**
         * Enables the leave call confirmation.
         */
        val ALWAYS_ENABLED = fromString("always_enable")

        /**
         * Disables the leave call confirmation.
         */
        val ALWAYS_DISABLED = fromString("always_disable")

        /**
         * Creates an instance of [CallCompositeLeaveCallConfirmationMode] from a string name.
         *
         * @param name The name of the mode as a string.
         * @return An instance of [CallCompositeLeaveCallConfirmationMode] corresponding to the provided name.
         */
        fun fromString(name: String?): CallCompositeLeaveCallConfirmationMode {
            return fromString(name, CallCompositeLeaveCallConfirmationMode::class.java)
        }

        /**
         * Returns all the available values for [CallCompositeLeaveCallConfirmationMode].
         *
         * @return A collection of all [CallCompositeLeaveCallConfirmationMode] values.
         */
        fun values(): Collection<CallCompositeLeaveCallConfirmationMode?>? {
            return values(CallCompositeLeaveCallConfirmationMode::class.java)
        }
    }
}
