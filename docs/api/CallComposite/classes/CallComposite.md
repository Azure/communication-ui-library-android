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

Start call composite experience with joining a Teams meeting.

```java
public void launch(Context context, TeamsMeetingOptions teamsMeetingOptions)
```

#### Parameters
* `teamsMeetingOptions` - The TeamsMeetingOptions used to locate the Teams meetings.


### `setOnErrorHandler`

Set a callback eventHandler to receive information about occurred errors.

```java
public void setOnErrorHandler(CallingEventHandler<<ErrorEvent<CommunicationUIErrorEvent>> eventHandler) 
```

#### Parameters
* `eventHandler` - The instance of CallingEventHandler<<ErrorEvent<CommunicationUIErrorEvent>> that will receive error information.  
       