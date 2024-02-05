// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CallCompositeSupportedLocaleTest {
    @Test
    public void supportedLocaleTest() {

        assertEquals("de", CallCompositeSupportedLocale.DE.getLanguage());
        assertEquals("de", CallCompositeSupportedLocale.DE_DE.getLanguage());
        assertEquals("DE", CallCompositeSupportedLocale.DE_DE.getCountry());

        assertEquals("en", CallCompositeSupportedLocale.EN.getLanguage());
        assertEquals("US", CallCompositeSupportedLocale.EN_US.getCountry());
        assertEquals("GB", CallCompositeSupportedLocale.EN_UK.getCountry());

        assertEquals("es", CallCompositeSupportedLocale.ES.getLanguage());
        assertEquals("ES", CallCompositeSupportedLocale.ES_ES.getCountry());

        assertEquals("fr", CallCompositeSupportedLocale.FR.getLanguage());
        assertEquals("FR", CallCompositeSupportedLocale.FR_FR.getCountry());

        assertEquals("it", CallCompositeSupportedLocale.IT.getLanguage());
        assertEquals("IT", CallCompositeSupportedLocale.IT_IT.getCountry());

        assertEquals("ja", CallCompositeSupportedLocale.JA.getLanguage());
        assertEquals("JP", CallCompositeSupportedLocale.JA_JP.getCountry());

        assertEquals("ko", CallCompositeSupportedLocale.KO.getLanguage());
        assertEquals("KR", CallCompositeSupportedLocale.KO_KR.getCountry());

        assertEquals("nl", CallCompositeSupportedLocale.NL.getLanguage());
        assertEquals("NL", CallCompositeSupportedLocale.NL_NL.getCountry());

        assertEquals("pt", CallCompositeSupportedLocale.PT.getLanguage());
        assertEquals("BR", CallCompositeSupportedLocale.PT_BR.getCountry());

        assertEquals("ru", CallCompositeSupportedLocale.RU.getLanguage());
        assertEquals("RU", CallCompositeSupportedLocale.RU_RU.getCountry());

        assertEquals("tr", CallCompositeSupportedLocale.TR.getLanguage());
        assertEquals("TR", CallCompositeSupportedLocale.TR_TR.getCountry());

        assertEquals("zh", CallCompositeSupportedLocale.ZH.getLanguage());
        assertEquals("CN", CallCompositeSupportedLocale.ZH_CN.getCountry());
        assertEquals("TW", CallCompositeSupportedLocale.ZH_TW.getCountry());
    }
}
