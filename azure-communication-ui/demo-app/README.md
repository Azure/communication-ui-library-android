# Azure Communication UI Mobile Library for Android Demo App

The sample app is a native Android application developed to demonstrate Azure Communication UI
library. Showcases use of both Java and Kotlin to run library.

## Getting Started

### Prerequisites

- An Azure account with an active
  subscription. [Create an account for free](https://azure.microsoft.com/free/?WT.mc_id=A261C142F).
- An OS running [Android Studio](https://developer.android.com/studio).
- A deployed Communication Services
  resource. [Create a Communication Services resource](https://docs.microsoft.com/azure/communication-services/quickstarts/create-communication-resource)
  .
- Azure Communication Services
  Token. [See example](https://docs.microsoft.com/azure/communication-services/tutorials/trusted-service-tutorial)
- (Optional) Create Azure Communication Services Token service
  URL. [See example](https://docs.microsoft.com/azure/communication-services/tutorials/trusted-service-tutorial)
  .

### Run Sample

1. Open azure-communication-ui folder in Android Studio
2. Select demo-app as a build configuration
3. (Optional) for your convinience you may configure default values for the app.
   Create `local.properties` file in the `/azure-communication-ui` directory:
    - `TOKEN_FUNCTION_URL`="..."  # the URL to request Azure Communication Services token
    - `ACS_TOKEN`="..."           # Azure Communication Services token
    - `ACS_IDENTITY`="..."  # Azure Communication Services identity
    - `USER_NAME`="..."           # your preferred display name
    - `GROUP_CALL_ID`="..."       # this a type of UUID used to start and join a meeting
    - `TEAMS_MEETING_LINK`="..."  # the URL to a Teams meeting
    - `END_POINT_URL`="..."       # the URL for chat end point
    - `IDENTITY`="..."            # the identity for chat
    - `THREAD_ID`="..."           # chat thread id
    - `PARTICIPANT_MRIS`="..."    # the MRIs for remote participants to dial
4. For build variants:
   You can add variants selection for build to indicate which demo app you want to run or debug (
   calling, chat, call-with-chat)
5. Build and Run

### 1 to N Calling Push Notifications Setup

1. Follow [QuickStart](https://learn.microsoft.com/en-us/azure/communication-services/how-tos/ui-library-sdk/one-to-one-calling?tabs=kotlin&pivots=platform-android) to setup Push Notification Hub for ACS Resource or EventGrid
2. For `demo-app` add `google-service.json`
3. In `local.properties` set `ENABLE_GOOGLE_SERVICES=true`
