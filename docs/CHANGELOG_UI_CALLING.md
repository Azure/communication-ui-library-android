# Azure Communication UI Calling Release History

## 1.1.0 (2022-11-09)
- This version is the public GA release with Calling UI Library

### Bug Fixes
- Error banner color for Dark theme corrected

## 1.1.0-beta.1 (2022-10-03)

### Features
- Setting up Call Title and Subtitle is now availble by customizing `CallCompositeLocalOptions` with `CallCompositeSetupScreenViewData`.
- Implemented new error message `cameraFailure` that can be sent to developers when initiating or turning on camera fails.
- Error message now shown when network is not available before joining a call.
- Added new button to allow user to quickly navigate to app's info page when permissions are denied.

### Bug Fixes
- Display DrawerDialog across screen rotation.
- Fix ANR when trying to hang up call on hold.
- Fix edge case with multiple activity instances.
- Fix display name not getting truncated in participant list when they are too long (https://github.com/Azure/communication-ui-library-android/pull/370).

## 1.0.0 (2022-06-20)
- This version is the public GA release with Calling UI Library

## 1.0.0-beta.3 (2022-06-15)

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
