package com.azure.android.communication.ui.configuration

class LocalizationConfiguration {
    var language: String = ""
    var isRTL: Boolean = false

    constructor (
        language: String,
        isRTL: Boolean
    ) {
        this.language = language
        this.isRTL = isRTL
    }

    constructor(language: String) {
        this.language = language
    }
}
