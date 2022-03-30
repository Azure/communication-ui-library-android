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
    final LanguageCode languageCode
)         
```

### Parameters
* `languageCode` - LanguageCode Enum(ie. LanguageCode.ITALIAN, LanguageCode.JAPANESE, LanguageCode.CHINESE_SIMPLIFIED...)


```java
public LocalizationConfiguration(
    final LanguageCode languageCode, 
    final int layoutDirection
) 
```

### Parameters
* `languageCode` - LanguageCode Enum(ie. LanguageCode.ITALIAN, LanguageCode.JAPANESE, LanguageCode.CHINESE_SIMPLIFIED ...)
* `layoutDiection` - int for layout direction. Default value is `LayoutDirection.LTR`.


## Methods

### `getSupportedLanguages`

The languages code list supported by `CallComposite`.

```java
public static Collection<LanguageCode> getSupportedLanguages() 
```

### `layoutDirection`

The layoutDirection int value to be used by `CallComposite`.

```java
public boolean layoutDirection() 
```
 
### `getLanguageCode`

The language code to be used by `CallComposite`.

```java
public LanguageCode getLanguageCode() 
```
