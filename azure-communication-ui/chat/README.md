![Hero Image](../../docs/media/mobile-ui-library-hero-image.png)

# Azure Communication UI Mobile Library for Android

Azure Communication [UI Mobile Library](https://docs.microsoft.com/en-us/azure/communication-services/concepts/ui-library/ui-library-overview) is an Azure Communication Services capability focused on providing UI components for common business-to-consumer and business-to-business calling interactions.

## Getting Started

Get started with Azure Communication Chat Services by using the UI Library to integrate communication experiences into your applications. For detailed instructions to quickly integrate the UI Library functionalities visit the [Quick-start Documentation](Chat-Quick-Start).


### Prerequisites

- An Azure account with an active subscription. [Create an account for free](https://azure.microsoft.com/free/?WT.mc_id=A261C142F).
- An OS running [Android Studio](https://developer.android.com/studio).
- A deployed Communication Services resource. [Create a Communication Services resource](https://docs.microsoft.com/azure/communication-services/quickstarts/create-communication-resource).
- Azure Communication Services Token. [See example](https://docs.microsoft.com/azure/communication-services/tutorials/trusted-service-tutorial)


### Install the packages

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
    implementation 'com.azure.android:azure-communication-ui-chat:0.1.0'
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

Create `ChatComposite` and launch it. Replace `<USER_ACCESS_TOKEN>` with your token and `<USER_ID>` with your identity string from your azure communication resource's Identity & User Access Tokens settings. Also replace `<DISPLAY_NAME>` with your name and `<THREAD_ID>` with your chat thread ID and `<ENDPOINT_URL>` with your endpoint from your communication resource. For full instructions check out our [quickstart]ANDROID_QUICKSTART_LINK) or get the completed [sample](AZURE-SAMPLE/Chat).

#### [Kotlin](#tab/kotlin)

```kotlin
val chatComposite = ChatCompositeBuilder().build()
val communicationTokenRefreshOptions =
    CommunicationTokenRefreshOptions("<USER_ACCESS_TOKEN>", true)
val communicationTokenCredential =
    CommunicationTokenCredential(communicationTokenRefreshOptions)
val locator = ChatCompositeJoinLocator("<THREAD_ID>", "<ENDPOINT_URL>")
val remoteOptions =
    ChatCompositeRemoteOptions(locator, communicationTokenCredential, "<USER_ID>", "<DISPLAY_NAME>")
chatComposite.launch(chatLauncherActivity, remoteOptions, ChatCompositeLocalOptions())
```

#### [Java](#tab/java)

```java
final ChatComposite chatComposite = new ChatCompositeBuilder().build();

final CommunicationTokenRefreshOptions communicationTokenRefreshOptions =
        new CommunicationTokenRefreshOptions("<USER_ACCESS_TOKEN>", true);
final CommunicationTokenCredential communicationTokenCredential =
        new CommunicationTokenCredential(communicationTokenRefreshOptions);

final ChatCompositeJoinLocator locator =
        new ChatCompositeJoinLocator("<THREAD_ID>", "<ENDPOINT_URL>");
final ChatCompositeRemoteOptions remoteOptions =
        new ChatCompositeRemoteOptions(locator, communicationTokenCredential, "<USER_ID>", "<DISPLAY_NAME>");
chatComposite.launch(chatLauncherActivity, remoteOptions, null);
```

For more details on Mobile UI Library functionalities visit the [API Reference Documentation](https://azure.github.io/azure-sdk-for-android/azure-communication-mobileui/index.html).


## Contributing to the Library

Before developing and contributing to Communication Mobile UI Library, check out our [making a contribution guide](../../docs/contributing-guide.md).  
Included in this repository is a demo of using Mobile UI Library to start a call and to start a chat. You can find the detail of using and developing the UI Library in the [Demo Guide](../../azure-communication-ui/demo-app).

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments. Also, please check our [Contribution Policy](../../docs/contributing-guide.md). 

## Community Help and Support

If you find a bug or have a feature request, please raise the issue on [GitHub Issues](https://github.com/Azure/communication-ui-library-android/issues).

## Known Issues

Please refer to the [wiki](https://github.com/Azure/communication-ui-library-android/wiki/Known-Issues) for known issues related to the library.