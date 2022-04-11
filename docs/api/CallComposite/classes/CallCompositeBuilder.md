**CLASS**

# `CallCompositeBuilder`

```java
public class CallCompositeBuilder
```

## Description

Builds an instance of CallComposite. 

## Methods

### `theme`

Set optional theme for call-composite to use by `CallComposite`.

```java
public CallCompositeBuilder theme(ThemeConfiguration theme) 
```

#### Parameters
* `ThemeConfiguration` - The `ThemeConfiguration` used by `CallComposite`.

### `customizeLocalization`

Set optional lcoalziation for call-composite to use by `CallComposite`.

```java
public CallCompositeBuilder localization(final LocalizationConfiguration localization)
```

#### Parameters
* `LocalizationConfiguration` - The `LocalizationConfiguration` used by `CallComposite`.

### `build`

Build instance of `CallComposite`.

```java
public CallComposite build()
```
       