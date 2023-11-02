package com.azure.android.communication.ui.callingcompositedemoapp

import com.azure.android.communication.common.CommunicationCloudEnvironment
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.CommunicationUserIdentifier
import com.azure.android.communication.common.MicrosoftTeamsUserIdentifier
import com.azure.android.communication.common.PhoneNumberIdentifier
import com.azure.android.communication.common.UnknownIdentifier


object Utilities {
    /**
     * This method is copied from IdentifierHelper in SDK
     * @param identifier to transform
     * @return RMI String for identifier
     */
    fun toMRI(identifier: CommunicationIdentifier): String {
        return if (identifier is CommunicationUserIdentifier) {
            identifier.id
        } else if (identifier is PhoneNumberIdentifier) {
            Constants.IDENTIFIER_PHONE_NUMBER_PREFIX + identifier.phoneNumber
        } else if (identifier is MicrosoftTeamsUserIdentifier) {
            if (identifier.rawId != null && identifier.rawId.isNotEmpty()) {
                identifier.rawId
            } else if (CommunicationCloudEnvironment.PUBLIC == identifier.cloudEnvironment) {
                Constants.IDENTIFIER_TEAMS_PUBLIC_PREFIX + identifier.userId
            } else if (CommunicationCloudEnvironment.DOD == identifier.cloudEnvironment) {
                Constants.IDENTIFIER_DOD_PREFIX + identifier.userId
            } else {
                if (CommunicationCloudEnvironment.GCCH == identifier.cloudEnvironment) Constants.IDENTIFIER_GCCH_PREFIX + identifier.userId else Constants.IDENTIFIER_TEAMS_VISITOR_PUBLIC_PREFIX + identifier.userId
            }
        } else {
            (identifier as UnknownIdentifier).id
        }
    }

    fun fromMri(mri: String): CommunicationIdentifier {
        if (mri.startsWith(Constants.IDENTIFIER_PHONE_NUMBER_PLUS_SIGN) ||
            mri.startsWith(Constants.IDENTIFIER_PHONE_NUMBER_PREFIX)
        ) {
            return PhoneNumberIdentifier(mri)
        } else if (mri.startsWith(Constants.IDENTIFIER_TEAMS_PUBLIC_PREFIX)) {
            return MicrosoftTeamsUserIdentifier(mri.substring(Constants.IDENTIFIER_TEAMS_PUBLIC_PREFIX.length)).setCloudEnvironment(
                CommunicationCloudEnvironment.PUBLIC
            )
        } else if (mri.startsWith(Constants.IDENTIFIER_DOD_PREFIX)) {
            return MicrosoftTeamsUserIdentifier(mri.substring(Constants.IDENTIFIER_DOD_PREFIX.length)).setCloudEnvironment(
                CommunicationCloudEnvironment.DOD
            )
        } else if (mri.startsWith(Constants.IDENTIFIER_GCCH_PREFIX)) {
            return MicrosoftTeamsUserIdentifier(mri.substring(Constants.IDENTIFIER_GCCH_PREFIX.length)).setCloudEnvironment(
                CommunicationCloudEnvironment.GCCH
            )
        } else if (mri.startsWith(Constants.IDENTIFIER_ACS_PUBLIC_PREFIX) ||
            mri.startsWith(Constants.IDENTIFIER_ACS_LEGACY_PUBLIC_PREFIX) ||
            mri.startsWith(Constants.IDENTIFIER_ACS_DOD_PREFIX) ||
            mri.startsWith(Constants.IDENTIFIER_ACS_GCCH_PREFIX)
        ) {
            return CommunicationUserIdentifier(mri)
        }
        return UnknownIdentifier(mri)
    }
}
