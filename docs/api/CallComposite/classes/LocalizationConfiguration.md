**CLASS**

# `LocalizationConfiguration`

```java
public class LocalizationConfiguration
```

## Description

A configuration to allow customizing localization.

## Constructor

```java
public LocalizationConfiguration(
    final String language, 
    final boolean isRightToLeft,
)         
```

### Parameters
* `language` - String representing the locale code (ie. en, fr,  zh-Hant, zh-Hans, ...)
* `isRightToLeft` - Boolean for mirroring layout for right-to-left. Default value is `false`.

## Constructor
Creates an instance of `LocalizationConfiguration` with related parameters. 

```java
public LocalizationConfiguration(
    final String language, 
    final boolean isRightToLeft,
    final Map<String, String> customTranslations
) 
```

### Parameters
* `language` - String representing the locale code (ie. en, fr,  zh-Hant, zh-Hans, ...)
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
 
### `getLanguage`

The language code to be used by `CallComposite`.

```java
public String getLanguage() 
```
