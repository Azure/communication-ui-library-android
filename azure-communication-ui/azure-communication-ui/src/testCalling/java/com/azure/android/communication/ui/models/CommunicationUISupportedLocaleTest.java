// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.models;

import static org.junit.Assert.assertEquals;

import com.azure.android.communication.ui.calling.models.CommunicationUISupportedLocale;

import org.junit.Test;

public class CommunicationUISupportedLocaleTest {
    @Test
    public void supportedLocaleTest() {

        assertEquals("de", CommunicationUISupportedLocale.DE.getLanguage());
        assertEquals("de", CommunicationUISupportedLocale.DE_DE.getLanguage());
        assertEquals("DE", CommunicationUISupportedLocale.DE_DE.getCountry());

        assertEquals("en", CommunicationUISupportedLocale.EN.getLanguage());
        assertEquals("US", CommunicationUISupportedLocale.EN_US.getCountry());
        assertEquals("GB", CommunicationUISupportedLocale.EN_UK.getCountry());

        assertEquals("es", CommunicationUISupportedLocale.ES.getLanguage());
        assertEquals("ES", CommunicationUISupportedLocale.ES_ES.getCountry());

        assertEquals("fr", CommunicationUISupportedLocale.FR.getLanguage());
        assertEquals("FR", CommunicationUISupportedLocale.FR_FR.getCountry());

        assertEquals("it", CommunicationUISupportedLocale.IT.getLanguage());
        assertEquals("IT", CommunicationUISupportedLocale.IT_IT.getCountry());

        assertEquals("ja", CommunicationUISupportedLocale.JA.getLanguage());
        assertEquals("JP", CommunicationUISupportedLocale.JA_JP.getCountry());

        assertEquals("ko", CommunicationUISupportedLocale.KO.getLanguage());
        assertEquals("KR", CommunicationUISupportedLocale.KO_KR.getCountry());

        assertEquals("nl", CommunicationUISupportedLocale.NL.getLanguage());
        assertEquals("NL", CommunicationUISupportedLocale.NL_NL.getCountry());

        assertEquals("pt", CommunicationUISupportedLocale.PT.getLanguage());
        assertEquals("BR", CommunicationUISupportedLocale.PT_BR.getCountry());

        assertEquals("ru", CommunicationUISupportedLocale.RU.getLanguage());
        assertEquals("RU", CommunicationUISupportedLocale.RU_RU.getCountry());

        assertEquals("tr", CommunicationUISupportedLocale.TR.getLanguage());
        assertEquals("TR", CommunicationUISupportedLocale.TR_TR.getCountry());

        assertEquals("zh", CommunicationUISupportedLocale.ZH.getLanguage());
        assertEquals("CN", CommunicationUISupportedLocale.ZH_CN.getCountry());
        assertEquals("TW", CommunicationUISupportedLocale.ZH_TW.getCountry());
    }
}
