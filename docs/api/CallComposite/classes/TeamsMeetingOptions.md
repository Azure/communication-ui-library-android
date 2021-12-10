**CLASS**

# `TeamsMeetingOptions`

```java
public class TeamsMeetingOptions
```

## Description

Options to start Teams meeting call experience using `CallComposite`. 

## Constructors

```java
public TeamsMeetingOptions(
    Context context, 
    CommunicationTokenCredential communicationTokenCredential, 
    String meetingLink
)
```

```java
public TeamsMeetingOptions(
    Context context,
    CommunicationTokenCredential communicationTokenCredential,
    String meetingLink,
    String displayName
)
```

## Methods

### `getCommunicationTokenCredential`

 The CommunicationTokenCredential used for communication service authentication.

```java
public CommunicationTokenCredential getCommunicationTokenCredential()
```

### `getDisplayName`

The display name of the local participant when joining the Teams meeting.

```java
public String getDisplayName()
```

### `getMeetingLink`

A string representing the full Teams meeting link to join.

```java
public String getMeetingLink()
```