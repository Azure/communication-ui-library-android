# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-addconfigurationdebugging
-keepattributes LineNumberTable,SourceFile,Signature,*Annotation*
-renamesourcefileattribute SourceFile
-printusage /Users/AlbertLo/projects/microsoft/azure/communication-ui-library-android-publicPreview/azure-communication-ui/usage.txt
-keep class com.skype.rt.** { *; }
-keep class com.skype.android.data.** { *; }
-keep class com.skype.android.video.render.** { *; }
-keep class com.skype.android.video.capture.** { *; }
-keep class com.skype.android.video.hw.codec.** { *; }
-keep class com.microsoft.dl.audio.** { *; }
-keep class com.microsoft.dl.video.capture.** { *; }
-keep class com.azure.android.communication.calling.** { *; }
-keep class com.azure.android.communication.ui.redux.action.** { *; }
-keep class com.microsoft.media.** { *; }
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

-keep class android.view.MenuItem
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
-keep class com.fasterxml.jackson.databind.deser.** { *; }


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
