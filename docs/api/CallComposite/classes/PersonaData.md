**CLASS**

# `PersonaData`

```java
public class PersonaData
```

## Description

PersonaData for local participant.

## Constructors

```java
public PersonaData(final Bitmap avatarBitmap)
```

```java
public PersonaData(final Bitmap avatarBitmap, final ImageView.ScaleType scaleType)
```

```java
public PersonaData(final String renderedDisplayName)
```

```java
public PersonaData(final String renderedDisplayName, final Bitmap avatarBitmap)
```

```java
public PersonaData(final String renderedDisplayName,
                                      final Bitmap avatarBitmap,
                                      final ImageView.ScaleType scaleType)
```

## Methods

### `getScaleType`

The scale type used by `PersonaData`.

```java
public ImageView.ScaleType getScaleType()
```

### `getRenderedDisplayName`

The rendered display name used by `PersonaData`.

```java
public String getRenderedDisplayName()
```

### `getAvatarBitmap`

The avatar bitmap used by `PersonaData`.

```java
public Bitmap getAvatarBitmap()
```