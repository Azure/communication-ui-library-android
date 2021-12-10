**CLASS**

# `GroupCallOptions`

```java
public class GroupCallOptions
```

## Description

Options to start group call experience using `CallComposite`. 

## Constructors

```java
public GroupCallOptions(
    Context context, 
    CommunicationTokenCredential communicationTokenCredential, 
    UUID groupId
)
```

```java
public GroupCallOptions(
    Context context, 
    CommunicationTokenCredential communicationTokenCredential, 
    UUID groupId, 
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

The display name of the local participant when joining the call.

```java
public String getDisplayName()
```

### `getGroupId`

The unique identifier for the group conversation.

```java
public UUID getGroupId()
```