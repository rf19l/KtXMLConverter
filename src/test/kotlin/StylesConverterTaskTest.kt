import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class StylesTaskTest {

    @TempDir
    lateinit var testProjectDir: File
    private lateinit var settingsFile: File
    private lateinit var buildFile: File
    private lateinit var resDir: File

    @BeforeEach
    fun setup() {
        settingsFile = testProjectDir.resolve("settings.gradle.kts")
        buildFile = testProjectDir.resolve("build.gradle.kts")
        resDir = File(testProjectDir, "src/main/res/values")
        resDir.mkdirs()
    }

    @Test
    fun `styles task generates expected output`() {
        // Set project name
        val projectName = "Example"
        val packageName = "com.org.example"

        // Setup test project
        // Write settings and build files
        settingsFile.writeText("""
            rootProject.name = "hello-world"
        """.trimIndent())

        buildFile.writeText("""
            plugins {
                id("com.rf.foster.ktxml")
            }
            
            ktXMLConverterExtension {
                projectName.set("$projectName")
                packageName.set("$packageName")
            }
        """.trimIndent())

        // Write dimens.xml to the project directory
        val dimensFile = File(resDir, "dimens.xml")
        dimensFile.writeText(
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <resources>
                    <dimen name="activity_horizontal_margin">16dp</dimen>
                    <dimen name="activity_vertical_margin">12dp</dimen>
                    <dimen name="appbar_padding">8dp</dimen>
                    <dimen name="fab_margin">16dp</dimen>
                    <dimen name="text_size_small">12sp</dimen>
                    <dimen name="text_size_medium">16sp</dimen>
                    <dimen name="text_size_large">20sp</dimen>
                </resources>
            """.trimIndent()
        )

        // Write colors.xml to the project directory
        val colorsFile = File(resDir, "colors.xml")
        colorsFile.writeText(
            """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <color name="colorPrimary">#3F51B5</color>
                <color name="colorPrimaryDark">#303F9F</color>
                <color name="colorAccent">#FF4081</color>
                <color name="textColorPrimary">#212121</color>
                <color name="textColorSecondary">#757575</color>
                <color name="dividerColor">#BDBDBD</color>
            </resources>
        """.trimIndent()
        )

        // Write styles.xml to the project directory
        val stylesFile = File(resDir, "styles.xml")
        stylesFile.writeText(
            """
                <resources>
                    <style name="TextAppearance.AppCompat.Headline">
                        <item name="android:textSize">@dimen/text_size_large</item>
                        <item name="android:textColor">@color/textColorPrimary</item>
                    </style>

                    <style name="TextAppearance.AppCompat.Subhead">
                        <item name="android:textSize">@dimen/text_size_medium</item>
                        <item name="android:textColor">@color/textColorSecondary</item>
                    </style>

                    <style name="TextAppearance.AppCompat.Body1">
                        <item name="android:textSize">@dimen/text_size_small</item>
                        <item name="android:textColor">@color/textColorPrimary</item>
                    </style>
                </resources>

            """.trimIndent()
        )

        // Run the tasks
        GradleRunner.create().withProjectDir(testProjectDir).withArguments("dimensTask", "colorsTask", "stylesTask", "--stacktrace")
            .withPluginClasspath().build()

        // Check the output
        val expectedOutput = """
    package com.org.example

    import androidx.compose.ui.text.TextStyle
    import com.org.example.ExampleColors
    import com.org.example.ExampleDimens

    object ExampleStyles {
        val textAppearanceAppCompatHeadline = TextStyle(
            fontSize = ExampleDimens.textSizeLarge,
            color = ExampleColors.colorPrimary,
        )
        val textAppearanceAppCompatSubhead = TextStyle(
            fontSize = ExampleDimens.textSizeMedium,
            color = ExampleColors.colorSecondary,
        )
        val textAppearanceAppCompatBody1 = TextStyle(
            fontSize = ExampleDimens.textSizeSmall,
            color = ExampleColors.colorPrimary,
        )
    }
""".trimIndent()

        val outputDir = File(testProjectDir, "build/generated/source/kapt/debug/com/org/example")
        val outputFile = File(outputDir, "${projectName}Styles.kt")
        Assertions.assertTrue(outputFile.exists())
        Assertions.assertEquals(expectedOutput, outputFile.readText().trim())
    }
}


