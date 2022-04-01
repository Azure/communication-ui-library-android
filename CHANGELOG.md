# Release History

## 1.0.0-beta.2 (2022-04-04)

### New Features
- Status bar color change for light and dark mode
- API 21, 22 support
- Screen share zoom support
- Localization support 
- Update joining experience processing indicator in setup view

### Breaking Changes
- Remove Context from GroupMeetingOptions() and TeamMeetingOptions()
- Add required parameter Context to CallComposite.launch()
- Rename ErrorEvent to CommunicationUIErrorEvent

### Bug Fixes
- Start service is crashing for API 31
- Long display name truncation on local participant display view
- Participant drawer is closed when participant is clicked
- Sort participants, append suffix for local Participant and handle unnamed participant for the Participant List

## 1.0.0-beta.1 (2021-12-08)
This is the initial release of Azure Communication UI Library. For more information, please see the [README][read_me] and [QuickStart][documentation].

This is a Public Preview version, so breaking changes are possible in subsequent releases as we improve the product. To provide feedback, please submit an issue in our [Issues](https://github.com/Azure/communication-ui-library-android/issues).

<!-- LINKS -->
[read_me]: https://github.com/Azure/communication-ui-library-android/blob/main/README.md
[documentation]: https://docs.microsoft.com/en-us/azure/communication-services/quickstarts/ui-library/get-started-call?tabs=kotlin&pivots=platform-android
