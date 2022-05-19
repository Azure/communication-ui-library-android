# Release History

## 1.0.0-beta.3 (2022-04-04)

### Bug Fixes
- Fixed crash when internal feature flag API was not initialized

## 1.0.0-beta.2 (2022-04-04)

### New Features
- Status bar color change for light and dark mode
- API 21, 22 support
- Screen share pinch zoom support
- Localization support
- Update joining experience to show call join processing indicator in setup view
- Bluetooth headphones support

### Breaking Changes
- Remove Context from GroupMeetingOptions() and TeamMeetingOptions()
- Add required parameter Context to CallComposite.launch()
- Rename ErrorEvent to CommunicationUIErrorEvent
- Rename `azure_communication_ui_calling_primary_color` to `azure_communication_ui_communication_primary` in Theme.

### Bug Fixes
- Start service is crashing for API 31
- Long display name truncation on local participant display view
- Sort participants, append suffix for local Participant and handle unnamed participant for the Participant List

## 1.0.0-beta.1 (2021-12-08)
This is the initial release of Azure Communication UI Library. For more information, please see the [README][read_me] and [QuickStart][documentation].

This is a Public Preview version, so breaking changes are possible in subsequent releases as we improve the product. To provide feedback, please submit an issue in our [Issues](https://github.com/Azure/communication-ui-library-android/issues).

<!-- LINKS -->
[read_me]: https://github.com/Azure/communication-ui-library-android/blob/main/README.md
[documentation]: https://docs.microsoft.com/en-us/azure/communication-services/quickstarts/ui-library/get-started-call?tabs=kotlin&pivots=platform-android
