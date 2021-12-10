**CLASS**

# `ErrorEvent`

```java
public class ErrorEvent<T>
```

## Description

Event with error type and caused throwable. 

## Methods

### `getCause`

Returns the cause of this `throwable` or `null` if the cause is nonexistent or unknown. (The cause is the throwable that caused this throwable to get thrown).

```java
public Throwable getCause()
```

### `getErrorType`

Returns the error code.

```java
public ErrorType<T> getErrorType()
```
       