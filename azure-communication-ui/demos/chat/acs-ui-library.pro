# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT License.

# comment out to enable additional app size reductions
-dontobfuscate

-keepattributes LineNumberTable,SourceFile,Signature,*Annotation*
-renamesourcefileattribute SourceFile
-keepclasseswithmembers public class com.azure.android.communication.ui.calling.models.CallCompositeSupportedLocale {
    public static <fields>;
}

# skypert.jar
-keep class com.skype.rt.** { *; }
# VideoHost.jar
-keep class com.skype.android.data.** { *; }
# PalVideo.jar
-keep class com.skype.android.video.render.** { *; }
# VideoHost.jar
-keep class com.skype.android.video.capture.** { *; }
# hw-video-coddec.jar
-keepclassmembers class com.skype.android.video.hw.** {
    <fields>;
    <methods>;
}
# dl-audio.jar
-keep class com.microsoft.dl.audio.** { *; }
# dl-video.jar
-keep class com.microsoft.dl.video.** { *; }
-keep class com.microsoft.dl.utils.** { *; }
-keep class com.azure.android.communication.calling.** { *; }
# palutils.jar
-keepclassmembers class com.microsoft.media.** {
    <fields>;
    <methods>;
}
-keep class com.skype.android.util2.** { *; }

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}
-keep,includedescriptorclasses class * {
    public static native <methods>;
}
-keep class com.fasterxml.jackson.databind.deser.** { *; }

-dontwarn com.microsoft.device.display.DisplayMask
-dontwarn java.beans.ConstructorProperties
-dontwarn java.beans.Transient
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.slf4j.impl.StaticMDCBinder
-dontwarn org.slf4j.impl.StaticMarkerBinder
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

##---------------End: proguard configuration for Gson  ----------
