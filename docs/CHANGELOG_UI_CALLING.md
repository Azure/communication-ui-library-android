# Azure Communication UI Calling Release History

## 1.3.0 (2023-04-06)

### Features
- Introducing skip setup screen call join experience with `skipSetupScreen` on `CallCompositeLocalOptions`
- Introducing camera and microphone configuration capability for initial call join configuration with `cameraOn` and `microphoneOn` on `CallCompositeLocalOptions`
- Ongoing call in background notification permission update for API 33

## 1.2.0 (2023-03-09)

### Features
- Introduced a Call History available with `DebugInfo` on the `CallComposite`

## 1.2.0-beta.1 (2022-11-30)

### Features
- Call Diagnostics information is available on the UI and on API via `CallComposite.getDebugInfo()`
- Enhancements support for TV Devices (Smaller PIP, Focus Navigation, Label improvements)
- Localization support for Arabic, Finnish, Hebrew, Norwegian Bokmål, Polish, Swedish

### Bug Fixes
- Crash fix for Xamarin when on end call button, cancel is pressed

## 1.1.0 (2022-11-09)

### New Features
- `CallCompositeSetupScreenViewData` introduced for setting up call title and subtitle.
- New error message `cameraFailure` added to address camera related errors.
- Joining call is prevented with a new Error message now when network is not available.
- Added permission setting capability to allow user to quickly navigate to app's info page when permissions are denied.

### Bug Fixes
- Fixed Error banner and banner text color for dark theme.
- Display DrawerDialog across screen rotation.
- Fix ANR when trying to hang up call on hold.
- Fix edge case with multiple activity instances.
- Fix display name not getting truncated in participant list when they are too long.

## 1.1.0 (2022-11-09)

### New Features
- `CallCompositeSetupScreenViewData` introduced for setting up call title and subtitle.
- New error message `cameraFailure` added to address camera related errors.
- Joining call is prevented with a new Error message now when network is not available.
- Added permission setting capability to allow user to quickly navigate to app's info page when permissions are denied.

### Bug Fixes
- Fixed Error banner and banner text color for dark theme.
- Display DrawerDialog across screen rotation.
- Fix ANR when trying to hang up call on hold.
- Fix edge case with multiple activity instances.
- Fix display name not getting truncated in participant list when they are too long.

## 1.1.0-beta.1 (2022-10-03)

### New Features
- Setting up Call Title and Subtitle is now availble by customizing `CallCompositeLocalOptions` with `CallCompositeSetupScreenViewData`.
- Implemented new error message `cameraFailure` that can be sent to developers when initiating or turning on camera fails.
- Error message now shown when network is not available before joining a call.
- Added new button to allow user to quickly navigate to app's info page when permissions are denied.

### Bug Fixes
- Display DrawerDialog across screen rotation.
- Fix ANR when trying to hang up call on hold.
- Fix edge case with multiple activity instances.
- Fix display name not getting truncated in participant list when they are too long

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
