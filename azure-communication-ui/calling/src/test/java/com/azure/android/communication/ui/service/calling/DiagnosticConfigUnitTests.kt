package com.azure.android.communication.ui.service.calling

import com.azure.android.communication.ui.calling.DiagnosticConfig
import org.junit.Assert
import org.junit.Test

internal class DiagnosticConfigUnitTests {
    private val expectedPrefix = "aca110/"
    private val expectedVersion = "${expectedPrefix}1.5.0-alpha.6"

    @Test
    fun test_Expected_Tag() {

        val diagnosticConfig = DiagnosticConfig()
        val tags = diagnosticConfig.tags

        Assert.assertArrayEquals(arrayOf(expectedVersion), tags)
    }

    @Test
    fun test_Tag_Is_Valid_Format() {
        val validationRegExPattern =
            "$expectedPrefix[0-9][0-9]?.[0-9][0-9]?.[0-9][0-9]?((-(alpha|beta))?(.[0-9][0-9]?)?)?"
        val appIdTagFormatCheck = Regex(validationRegExPattern)
        val diagnosticConfig = DiagnosticConfig()
        val tags = diagnosticConfig.tags

        Assert.assertTrue(tags.size == 1)
        Assert.assertTrue(appIdTagFormatCheck.matches(tags[0]))
    }
}
