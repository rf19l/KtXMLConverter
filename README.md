# KtXMLConverter Gradle Plugin

The `KtXMLConverter` is a Gradle plugin that converts traditional Android XML resources (such as dimensions, colors, and styles) into Kotlin objects. This is especially useful when migrating to Jetpack Compose where you'd prefer to use Kotlin objects over XML resources.

## Features

- Converts `dimens.xml`, `colors.xml` and `styles.xml` to Kotlin objects.
- Generates `.kt` files inside your `build/generated/source/kapt/debug` directory.
- Automatically converts snake_case resource names to camelCase in the generated Kotlin code.
- Ensures that your Jetpack Compose codebase adheres to the Kotlin idiomatic way of doing things.

## Setup

To use this plugin, you need to include it in your project.

1. **Add the plugin to your `build.gradle.kts` (Kotlin DSL):**

```kotlin
plugins {
    id("com.rf.foster.ktxml.KtXMLConverter") version "x.y.z"
}
```
### Configure The Plugin
Pass your application name and package to tell the plugin what to name and where to put the generated files
```
ktXMLConverterExtension {
projectName.set("MyApp")
packageName.set("com.example.myapp")
}
```

### Usage
You can run each conversion task separately or run as one task
- Individually
    ```
    ./gradlew konvertDimens
    ./gradlew konvertColors
    ./gradlew konvertStyles
    ```
- All at once
````
./gradlew konvertXmlResources
````

### Output
The plugin will generate the following Kotlin objects in your build/generated/source/kapt/debug directory:

- MyAppDimens.kt for dimensions
- MyAppColors.kt for colors
- MyAppStyles.kt for styles
 
The generated files will be packaged under the package name you specified.

### Notes
The plugin assumes that your resource XML files are located in the src/main/res/values directory.

If your resources are located elsewhere, you'll need to adjust the file paths accordingly in the plugin code.

Ensure you replace "x.y.z" with the latest version of the plugin.

### Contribute
Contributions are welcome! Please open an issue if you encounter any problems or have a feature request. Any pull requests should include testing of added functionality.