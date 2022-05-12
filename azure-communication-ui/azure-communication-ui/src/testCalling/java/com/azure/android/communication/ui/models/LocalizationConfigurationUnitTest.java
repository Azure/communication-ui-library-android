// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import android.util.LayoutDirection;

import com.azure.android.communication.ui.calling.models.CommunicationUISupportedLocale;
import com.azure.android.communication.ui.calling.models.LocalizationConfiguration;

import org.junit.Test;

import java.util.Locale;

public class LocalizationConfigurationUnitTest {
    @Test
    public void localizationLocaleApiTest() {
        final LocalizationConfiguration localizationConfiguration = new LocalizationConfiguration(
                CommunicationUISupportedLocale.EN_UK
        );
        assertEquals("en", localizationConfiguration.getLocale().getLanguage());
        assertEquals("GB", localizationConfiguration.getLocale().getCountry());
    }

    @Test
    public void localizationLayoutDirectionAPITest() {
        final LocalizationConfiguration localizationConfiguration = new LocalizationConfiguration(
                CommunicationUISupportedLocale.EN_UK, LayoutDirection.LTR
        );
        assertEquals("en", localizationConfiguration.getLocale().getLanguage());
        assertEquals("GB", localizationConfiguration.getLocale().getCountry());
        assertEquals(LayoutDirection.LTR, (int) localizationConfiguration.getLayoutDirection());
    }

    @Test
    public void localizationOnlyLanguageLocaleAPITest() {
        final LocalizationConfiguration localizationConfiguration = new LocalizationConfiguration(
                Locale.ENGLISH
        );
        assertEquals("en", localizationConfiguration.getLocale().getLanguage());
        assertEquals("", localizationConfiguration.getLocale().getCountry());
    }

    @Test(expected = AssertionError.class)
    public void localizationAPINullParamsTest() {
        assertThrows(IllegalArgumentException.class, () -> new LocalizationConfiguration(null));
    }
}
