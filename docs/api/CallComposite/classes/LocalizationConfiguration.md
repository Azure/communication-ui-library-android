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
    final String languageCode, 
    final boolean isRightToLeft,
)         
```

### Parameters
* `languageCode` - String representing the locale code (ie. en, fr,  zh-Hant, zh-Hans, ...)


```java
public LocalizationConfiguration(
    final String languageCode, 
    final boolean isRightToLeft,
)         
```

### Parameters
* `languageCode` - String representing the locale code (ie. en, fr,  zh-Hant, zh-Hans, ...)
* `isRightToLeft` - Boolean for mirroring layout for right-to-left. Default value is `false`.

```java
public LocalizationConfiguration(
    final String languageCode, 
    final boolean isRightToLeft,
    final Map<String, String> customTranslations
) 
```

### Parameters
* `languageCode` - String representing the locale code (ie. en, fr,  zh-Hant, zh-Hans, ...)
* `customTranslations` - A dictionary of key-value pairs to override override predefined Call Composite's localization string. The key of the string should be matched with the one in AzureCommunicationUI
* `isRightToLeft` - Boolean for mirroring layout for right-to-left. Default value is `false`.

## Methods

### `getSupportedLanguages`

The languages code list supported by `CallComposite`.

```java
public List<String> getSupportedLanguages() 
```

### `isRightToLeft`

The isRightToLeft boolean value to be used by `CallComposite`.

```java
public boolean isRightToLeft() 
```
 
### `getLanguageCode`

The language code to be used by `CallComposite`.

```java
public String getLanguageCode() 
```
