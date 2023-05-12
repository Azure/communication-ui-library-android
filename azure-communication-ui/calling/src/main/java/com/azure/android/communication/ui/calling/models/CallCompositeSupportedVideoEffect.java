// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.calling.BackgroundBlurEffect;
import com.azure.android.communication.calling.VideoEffect;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;

public class CallCompositeSupportedVideoEffect {

    public static final VideoEffect NONE = null;
    public static final VideoEffect BACKGROUNDBLUR_EFFECT = new BackgroundBlurEffect();

    /**
     * Gets the collection of supported video effects as {@link VideoEffect}.
     *
     * @return collection of all supported VideoEffects.
     */
    public static Collection<VideoEffect> getSupportedVideoEffects() {
        final List<Field> fields = CollectionsKt.filter(
                Arrays.asList(CallCompositeSupportedVideoEffect.class.getDeclaredFields()),
                new Function1<Member, Boolean>() {
                    @Override
                    public Boolean invoke(final Member member) {
                        return Modifier.isStatic(member.getModifiers())
                                && Modifier.isFinal(member.getModifiers());
                    }
                }
        );
        return CollectionsKt.map(fields, new Function1<Field, VideoEffect>() {
            @Override
            public VideoEffect invoke(final Field field) {
                try {
                    return (VideoEffect) field.get(VideoEffect.class);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}
