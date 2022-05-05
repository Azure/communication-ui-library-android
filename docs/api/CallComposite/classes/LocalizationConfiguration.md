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
