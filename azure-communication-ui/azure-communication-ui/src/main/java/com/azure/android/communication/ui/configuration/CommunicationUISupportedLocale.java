// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;

/**
 * Defines locale for each supported language
 */
public final class CommunicationUISupportedLocale {

    public static final Locale EN = new Locale("en");
    public static final Locale EN_US = new Locale("en", "US");
    public static final Locale EN_UK = new Locale("en", "GB");
    public static final Locale ZH_CN = new Locale("zh", "CN");
    public static final Locale ZH_TW = new Locale("zh", "TW");
    public static final Locale ES = new Locale("es");
    public static final Locale ES_ES = new Locale("es", "ES");
    public static final Locale RU = new Locale("ru");
    public static final Locale RU_RU = new Locale("ru", "RU");
    public static final Locale JA = new Locale("ja");
    public static final Locale JA_JP = new Locale("ja", "JP");
    public static final Locale FR = new Locale("fr");
    public static final Locale FR_FR = new Locale("fr", "FR");
    public static final Locale PT = new Locale("pt");
    public static final Locale PT_BR = new Locale("pt", "BR");
    public static final Locale DE = new Locale("de");
    public static final Locale DE_DE = new Locale("de", "DE");
    public static final Locale KO = new Locale("ko");
    public static final Locale KO_KR = new Locale("ko", "KR");
    public static final Locale IT = new Locale("it");
    public static final Locale IT_IT = new Locale("it", "IT");
    public static final Locale NL = new Locale("nl");
    public static final Locale NL_NL = new Locale("nl", "NL");
    public static final Locale TR = new Locale("tr");
    public static final Locale TR_TR = new Locale("tr", "TR");

    /**
     * Gets the collection of supported languages as {@link Locale}
     *
     * @return collection of all supported Locale
     */
    public static Collection<Locale> getSupportedLocales() {
        final List<Field> fields = CollectionsKt.filter(
                Arrays.asList(CommunicationUISupportedLocale.class.getDeclaredFields()),
                new Function1<Member, Boolean>() {
                    @Override
                    public Boolean invoke(final Member member) {
                        return Modifier.isStatic(member.getModifiers())
                                && Modifier.isFinal(member.getModifiers());
                    }
                }
        );
        return CollectionsKt.map(fields, new Function1<Field, Locale>() {
            @Override
            public Locale invoke(final Field field) {
                try {
                    return (Locale) field.get(Locale.class);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}

