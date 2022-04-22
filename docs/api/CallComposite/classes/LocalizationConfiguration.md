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
    final String country 
)         
```

### Parameters
* `language` - Language String (ie. "fr", "en")
* `country` - Country String (ie. "FR", "US")


## Methods

### `getLayoutDirection`

The layoutDirection int value to be used by `CallComposite`.

```java
public boolean getLayoutDirection() 
```
 
### `getLocale`

The language code to be used by `CallComposite`.

```java
public LanguageCode getLocale() 
```
