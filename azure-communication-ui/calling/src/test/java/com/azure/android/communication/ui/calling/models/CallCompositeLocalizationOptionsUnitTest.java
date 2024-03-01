// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import android.util.LayoutDirection;

import org.junit.Test;

import java.util.Locale;

public class CallCompositeLocalizationOptionsUnitTest {
    @Test
    public void localizationLocaleApiTest() {
        final CallCompositeLocalizationOptions localizationConfiguration =
                new CallCompositeLocalizationOptions(
                CallCompositeSupportedLocale.EN_UK
        );
        assertEquals("en", localizationConfiguration.getLocale().getLanguage());
        assertEquals("GB", localizationConfiguration.getLocale().getCountry());
    }

    @Test
    public void localizationLayoutDirectionAPITest() {
        final CallCompositeLocalizationOptions localizationConfiguration =
                new CallCompositeLocalizationOptions(
                CallCompositeSupportedLocale.EN_UK, LayoutDirection.LTR
        );
        assertEquals("en", localizationConfiguration.getLocale().getLanguage());
        assertEquals("GB", localizationConfiguration.getLocale().getCountry());
        assertEquals(LayoutDirection.LTR, (int) localizationConfiguration.getLayoutDirection());
    }

    @Test
    public void localizationOnlyLanguageLocaleAPITest() {
        final CallCompositeLocalizationOptions localizationConfiguration =
                new CallCompositeLocalizationOptions(
                Locale.ENGLISH
        );
        assertEquals("en", localizationConfiguration.getLocale().getLanguage());
        assertEquals("", localizationConfiguration.getLocale().getCountry());
    }

    @Test(expected = AssertionError.class)
    public void localizationAPINullParamsTest() {
        assertThrows(IllegalArgumentException.class, () -> new CallCompositeLocalizationOptions(null));
    }
}
