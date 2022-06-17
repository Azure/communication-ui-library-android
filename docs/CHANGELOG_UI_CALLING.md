# Azure Communication UI Calling Release History


## 1.0.0 (Unreleased)

### Breaking Changes
- `CallComposite.setOnErrorHandler` is replaced with `CallComposite.addOnErrorEventHandler`
- `CallComposite.setOnRemoteParticipantJoinedHandler` is replaced with `CallComposite.addOnRemoteParticipantJoinedEventHandler`
 

## 1.0.0-beta.2 (2022-06-13)

### New Features
- Call on hold
- UI support for tablets

### Breaking Changes
- `CommunicationUIEventCode` class is removed
- Added "CallComposite" as a prefix to public classes
- `CallComposite.launch` functions are refactored. Argument classes `GroupCallOptions`, `TeamsMeetingOptions` are removed. New argument classes are added: `CallCompositeRemoteOptions`, `CallCompositeLocalOptions`
 

## 1.0.0-beta.1 (2022-05-18)

This is the initial release of Azure Communication UI Calling Library. For more information, please see the [README][read_me] and [QuickStart][documentation].

This is a Public Preview version, so breaking changes are possible in subsequent releases as we improve the product. To provide feedback, please submit an issue in our [Issues](https://github.com/Azure/communication-ui-library-android/issues).

<!-- LINKS -->
[read_me]: https://github.com/Azure/communication-ui-library-android/blob/main/README.md
[documentation]: https://docs.microsoft.com/en-us/azure/communication-services/quickstarts/ui-library/get-started-call?tabs=kotlin&pivots=platform-android
