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
    CommunicationTokenCredential cedential, 
    UUID groupId
)
```

```java
public GroupCallOptions(
    CommunicationTokenCredential cedential, 
    UUID groupId, 
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

The display name of the local participant when joining the call.

```java
public String getDisplayName()
```

### `getGroupId`

The unique identifier for the group conversation.

```java
public UUID getGroupId()
```