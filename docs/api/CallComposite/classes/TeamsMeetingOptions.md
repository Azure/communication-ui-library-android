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
    CommunicationTokenCredential credential, 
    String meetingLink
)
```

```java
public TeamsMeetingOptions(
    CommunicationTokenCredential credential,
    String meetingLink,
    String displayName
)
```

## Methods

### `getCredential`

 The CommunicationTokenCredential used for communication service authentication.

```java
public CommunicationTokenCredential getCredential()
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