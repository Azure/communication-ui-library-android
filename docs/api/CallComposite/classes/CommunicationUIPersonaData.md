**CLASS**

# `CommunicationUIPersonaData`

```java
public class CommunicationUIPersonaData
```

## Description

PersonaData for local participant.

## Constructors

```java
public CommunicationUIPersonaData(final Bitmap avatarBitmap)
```

```java
public CommunicationUIPersonaData(final Bitmap avatarBitmap, final ImageView.ScaleType scaleType)
```

```java
public CommunicationUIPersonaData(final String renderedDisplayName)
```

```java
public CommunicationUIPersonaData(final String renderedDisplayName, final Bitmap avatarBitmap)
```

```java
public CommunicationUIPersonaData(final String renderedDisplayName,
                                      final Bitmap avatarBitmap,
                                      final ImageView.ScaleType scaleType)
```

## Methods

### `getScaleType`

The scale type used by `CommunicationUIPersonaData`.

```java
public ImageView.ScaleType getScaleType()
```

### `getRenderedDisplayName`

The rendered display name used by `CommunicationUIPersonaData`.

```java
public String getRenderedDisplayName()
```

### `getAvatarBitmap`

The avatar bitmap used by `CommunicationUIPersonaData`.

```java
public Bitmap getAvatarBitmap()
```