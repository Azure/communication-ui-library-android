# Release History

## 1.0.0-beta.4 (upcoming)

### New Features
- Local participant persona injection [#180](https://github.com/Azure/communication-ui-library-android/pull/180)

### Breaking Changes
- Renamed `azure_communication_ui_communication_primary` to `azure_communication_ui_primary_color` in Theme. [#208](https://github.com/Azure/communication-ui-library-android/pull/208)

## 1.0.0-beta.3 (2022-04-04)

### Bug Fixes
- Fixed crash when internal feature flag API was not initialized [#175](https://github.com/Azure/communication-ui-library-android/pull/175)

## 1.0.0-beta.2 (2022-04-04)

### New Features
- Status bar color change for light and dark mode [#9](https://github.com/Azure/communication-ui-library-android/pull/9)
- API 21, 22 support [#31](https://github.com/Azure/communication-ui-library-android/pull/31)
- Screen share pinch zoom support [#38](https://github.com/Azure/communication-ui-library-android/pull/38)
- Localization support [#68](https://github.com/Azure/communication-ui-library-android/pull/68)
- Update joining experience to show call join processing indicator in setup view [#57](https://github.com/Azure/communication-ui-library-android/pull/57)
- Bluetooth headphones support [#40](https://github.com/Azure/communication-ui-library-android/pull/40)

### Breaking Changes
- Remove Context from GroupMeetingOptions() and TeamMeetingOptions() [#17](https://github.com/Azure/communication-ui-library-android/pull/17)
- Add required parameter Context to CallComposite.launch() [#17](https://github.com/Azure/communication-ui-library-android/pull/17)
- Rename ErrorEvent to CommunicationUIErrorEvent [#156](https://github.com/Azure/communication-ui-library-android/pull/156)
- Rename `azure_communication_ui_calling_primary_color` to `azure_communication_ui_communication_primary` in Theme. [#59](https://github.com/Azure/communication-ui-library-android/pull/59)

### Bug Fixes
- Start service is crashing for API 31 [#10](https://github.com/Azure/communication-ui-library-android/pull/10)
- Long display name truncation on local participant display view [#13](https://github.com/Azure/communication-ui-library-android/pull/13) 
- Sort participants, append suffix for local Participant and handle unnamed participant for the Participant List [#8](https://github.com/Azure/communication-ui-library-android/pull/8)

## 1.0.0-beta.1 (2021-12-08)
This is the initial release of Azure Communication UI Library. For more information, please see the [README][read_me] and [QuickStart][documentation].

This is a Public Preview version, so breaking changes are possible in subsequent releases as we improve the product. To provide feedback, please submit an issue in our [Issues](https://github.com/Azure/communication-ui-library-android/issues).

<!-- LINKS -->
[read_me]: https://github.com/Azure/communication-ui-library-android/blob/main/README.md
[documentation]: https://docs.microsoft.com/en-us/azure/communication-services/quickstarts/ui-library/get-started-call?tabs=kotlin&pivots=platform-android
