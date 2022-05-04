**CLASS**

# `LocalizationConfiguration`

```java
public class LocalizationConfiguration
```

## Description

A configuration to allow customizing localization.

## Constructor

Creates an instance of `LocalizationConfiguration` with related parameters. 

```java
public LocalizationConfiguration(
    final Locale locale
)         
```

### Parameters
* `locale` - Locale (ie. Locale.US)


```java
public LocalizationConfiguration(
    final Locale locale, 
    final int layoutDirection
) 
```

### Parameters
* `locale` - Locale (ie. Locale.US)
* `layoutDiection` - int for layout direction. Default value is `LayoutDirection.LTR`.


```java
public LocalizationConfiguration(
    final String language
)         
```

### Parameters
* `language` - Language String (ie. "fr", "en")


```java
public LocalizationConfiguration(
    final String language,
    final int layoutDirection
)         
```

### Parameters
* `language` - Language String (ie. "fr", "en")
* `layoutDiection` - int for layout direction. Default value is `LayoutDirection.LTR`.
  

```java
public LocalizationConfiguration(
    final String language,
    final String countryCode
)         
```

### Parameters
* `language` - Language String (ie. "fr", "en")
* `countryCode` - Country code String (ie. "FR", "US")

```java
public LocalizationConfiguration(
    final String language,
    final String countryCode,
    final int layoutDirection
)         
```

### Parameters
* `language` - Language String (ie. "fr", "en")
* `countryCode` - Country code String (ie. "FR", "US")
* `layoutDiection` - int for layout direction. Default value is `LayoutDirection.LTR`.

## Methods

### `getLayoutDirection`

The layoutDirection Integer value to be used by `CallComposite`.

```java
public Integer getLayoutDirection() 
```
 
### `getLocale`

The Locale to be used by `CallComposite`.

```java
public Locale getLocale() 
```
