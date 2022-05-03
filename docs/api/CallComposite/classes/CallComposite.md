**CLASS**

# `CallComposite`

```java
public class CallComposite
```

## Description

This is the main class representing the entry point for the Call Composite. 


## Methods

### `launch`

Start call composite experience with joining a group call.

```java
public void launch(Context context, GroupCallOptions groupCallOptions) 
```

#### Parameters
* `groupCallOptions` - The GroupCallOptions used to locate the group call.  


```java
public void launch(Context context, GroupCallOptions groupCallOptions, LocalDataOptions localDataOptions)
```

#### Parameters
* `localDataOptions` - The options used to set local participants persona data.

Start call composite experience with joining a Teams meeting.

```java
public void launch(Context context, TeamsMeetingOptions teamsMeetingOptions)
```

#### Parameters
* `teamsMeetingOptions` - The TeamsMeetingOptions used to locate the Teams meetings.

```java
public void launch(Context context, TeamsMeetingOptions teamsMeetingOptions, LocalDataOptions localDataOptions)
```

#### Parameters
* `localDataOptions` - The options used to set local participants persona data.

### `setOnErrorHandler`

Set a callback eventHandler to receive information about occurred errors.

```java
public void setOnErrorHandler(CallingEventHandler<CommunicationUIErrorEvent> eventHandler) 
```

#### Parameters
* `eventHandler` - The instance of CallingEventHandler that will receive error information.  
       
### `setOnRemoteParticipantJoinedHandler`

Set a callback eventHandler to receive information about remote participant join.

```java
public void setOnRemoteParticipantJoinedHandler(CallingEventHandler<CommunicationUIRemoteParticipantJoinedEvent> eventHandler) 
```

#### Parameters
* `eventHandler` - The instance of CallingEventHandler that will receive participant join information.  

### `setRemoteParticipantPersonaData`

Set remote participant persona data.

```java
public SetPersonaDataResult setRemoteParticipantPersonaData(CommunicationIdentifier identifier, PersonaData personaData) 
```

#### Parameters
* `identifier` - The instance of CommunicationIdentifier to identify remote participant.  
* `personaData` - The instance of personaData that will contain remote participant persona data.  