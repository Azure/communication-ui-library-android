![A banner image that shows some use cases of the Calling library](../../docs/media/mobile-ui-library-calling-hero-image.png?raw=true)

# Azure Communication UI Mobile Library for Android - Calling

## Latest Release

- [1.8.0 release](https://github.com/Azure/communication-ui-library-android/releases/tag/calling-v1.8.0)

## Getting Started

Get started with Azure Communication Services by using the Calling UI Library to integrate communication experiences into your applications. For detailed instructions to quickly integrate the Calling UI Library functionalities visit the [Quick-start Documentation](https://docs.microsoft.com/en-us/azure/communication-services/quickstarts/ui-library/get-started-composites?tabs=kotlin&pivots=platform-android).

## Installation

In your app level (**app folder**) `build.gradle`, add the following lines to the dependencies and android sections.

```groovy
android {
    ...
    packagingOptions {
        pickFirst  'META-INF/*'
    }
    ...
}
```

```groovy
dependencies {
    ...
    implementation 'com.azure.android:azure-communication-ui-calling:1.8.0'
    ...
}
```

In your project gradle scripts add following lines to `repositories`. For `Android Studio (2020.*)` the `repositories` are in `settings.gradle` `dependencyResolutionManagement(Gradle version 6.8 or greater)`. If you are using old versions of `Android Studio (4.*)` then the `repositories` will be in project level `build.gradle` `allprojects{}`.

```groovy
repositories {
    ...
    mavenCentral()
    maven {
        url "https://pkgs.dev.azure.com/MicrosoftDeviceSDK/DuoSDK-Public/_packaging/Duo-SDK-Feed/maven/v1"
    }
    ...
}
```
Sync project with gradle files. (Android Studio -> File -> Sync Project With Gradle Files)


### Quick Sample 

Create `CallComposite` and launch it. Replace `<GROUP_CALL_ID>` with your group ID for your call, `<DISPLAY_NAME>` with your name, and  `<USER_ACCESS_TOKEN>` with your token. For full instructions check out our [quickstart](https://docs.microsoft.com/azure/communication-services/quickstarts/ui-library/get-started-composites?tabs=kotlin&pivots=platform-android) or get the completed [sample](https://github.com/Azure-Samples/communication-services-android-quickstarts/tree/main/ui-calling).

#### [Kotlin](#tab/kotlin)

```kotlin
val communicationTokenRefreshOptions = CommunicationTokenRefreshOptions({ "<USER_ACCESS_TOKEN>" }, true)
val communicationTokenCredential = CommunicationTokenCredential(communicationTokenRefreshOptions)

val locator: CallCompositeJoinLocator = CallCompositeGroupCallLocator(UUID.fromString("GROUP_CALL_ID"))
val callComposite: CallComposite = CallCompositeBuilder()
    .applicationContext(this.applicationContext)
    .credential(communicationTokenCredential)
    .displayName("DISPLAY_NAME").build()

callComposite.launch(this, locator)
```

#### [Java](#tab/java)

```java
CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
        new CommunicationTokenRefreshOptions(() -> "<USER_ACCESS_TOKEN>", true);

CommunicationTokenCredential communicationTokenCredential = 
        new CommunicationTokenCredential(communicationTokenRefreshOptions);

final CallCompositeJoinLocator locator = new CallCompositeGroupCallLocator(UUID.fromString("GROUP_CALL_ID"));

CallComposite callComposite = new CallCompositeBuilder()
        .applicationContext(this.getApplicationContext())
        .credential(communicationTokenCredential)
        .displayName("DISPLAY_NAME").build();
callComposite.launch(this, locator);
```

For more details on Mobile UI Library functionalities visit the [API Reference Documentation](https://azure.github.io/azure-sdk-for-android/azure-communication-ui-calling).

### Accessibility

Previous Android API devices could perform accessibility differently comparing to the latest version. We ran through accessibility testing on previous Android API (26, 27, 28) devices to detect the possible differences on accessibility performance.

#### [API 26](#tab/API26)
```API 26 
When focusing on buttons, screen reader will not announce "double tap to activate".
There is no initial focus on setup screen.
The state/selected change for audio device select menu and video/mic/switch camera buttons may not be announced.
The snackbar on setup screen with error message will not be focused and announced.
```

#### [API 27/28](#tab/API27_28)
``` API 27/28
The state/selected change for audio device select menu and video/mic/switch camera buttons may not be announced.
The snackbar on setup screen with error message may take more time to show up. 
```

## Known Issues

Please refer to the [wiki](https://github.com/Azure/communication-ui-library-android/wiki/Known-Issues) for known issues related to the library.

## Further Reading

* [Azure Communication UI Library Conceptual Documentation](https://docs.microsoft.com/azure/communication-services/concepts/ui-framework/ui-sdk-overview)
* [Azure Communication Service](https://docs.microsoft.com/en-us/azure/communication-services/overview)
* [Azure Communication Client and Server Architecture](https://docs.microsoft.com/en-us/azure/communication-services/concepts/client-and-server-architecture)
* [Azure Communication Authentication](https://docs.microsoft.com/en-us/azure/communication-services/concepts/authentication)
* [Azure Communication Service Troubleshooting](https://docs.microsoft.com/en-us/azure/communication-services/concepts/troubleshooting-info)
* [Azure Communication Service UI Calling Library Maven Releases](https://search.maven.org/artifact/com.azure.android/azure-communication-ui-calling)
* [Azure Communication Service Android Calling Hero Sample](https://github.com/Azure-Samples/communication-services-android-calling-hero)
