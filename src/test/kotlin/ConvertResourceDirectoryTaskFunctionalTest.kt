import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class ConvertResourceDirectoryTaskFunctionalTest {

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
    fun `convertResourceDir runs all tasks and generates expected outputs`(@TempDir tempDir: Path) {
        // Set project name
        val projectName = "NewExample"
        val packageName = "com.org.newexample"

        // Setup test project
        // Write settings and build files
        settingsFile.writeText("""
        rootProject.name = "new-world"
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
                <dimen name="font_size_large">18sp</dimen>
                <dimen name="font_size_small">12sp</dimen>
                <dimen name="line_height_large">24sp</dimen>
                <dimen name="line_height_small">16sp</dimen>
                <dimen name="letter_spacing_normal">0.025</dimen>
                <dimen name="letter_spacing_large">0.05</dimen>
            </resources>
        """.trimIndent()
        )

        // Write colors.xml to the project directory
        val colorsFile = File(resDir, "colors.xml")
        colorsFile.writeText(
            """
        <?xml version="1.0" encoding="utf-8"?>
        <resources>
            <color name="textColorPrimary">#000000</color>
            <color name="textColorSecondary">#808080</color>
            <color name="colorAccent">#FF4081</color>
            <color name="colorPrimary">#3F51B5</color>
        </resources>
    """.trimIndent()
        )

        // Write styles.xml to the project directory
        val stylesFile = File(resDir, "styles.xml")
        stylesFile.writeText(
            """
            <resources>
                <style name="MyStyle">
                    <item name="android:textColor">@color/textColorPrimary</item>
                    <item name="android:textSize">@dimen/font_size_large</item>
                    <item name="android:textStyle">bold</item>
                    <item name="lineHeight">@dimen/line_height_large</item>
                    <item name="android:letterSpacing">@dimen/letter_spacing_normal</item>
                </style>
                <style name="AnotherStyle">
                    <item name="android:textColor">@color/textColorSecondary</item>
                    <item name="android:textSize">@dimen/font_size_small</item>
                    <item name="android:textStyle">italic</item>
                    <item name="lineHeight">@dimen/line_height_small</item>
                    <item name="android:letterSpacing">@dimen/letter_spacing_large</item>
                </style>
            </resources>

        """.trimIndent()
        )


        // Write dimens.xml, colors.xml and styles.xml to the project directory
        // Similar to your provided test

        // Run the convertResourceDir task
        GradleRunner.create().withProjectDir(testProjectDir).withArguments("konvertXmlResources", "--stacktrace")
            .withPluginClasspath().build()

        // Check the outputs
        val outputDir = File(testProjectDir, "build/generated/source/kapt/debug/com/org/newexample")

        val dimensOutputFile = File(outputDir, "${projectName}Dimens.kt")
        val colorsOutputFile = File(outputDir, "${projectName}Colors.kt")
        val stylesOutputFile = File(outputDir, "${projectName}Styles.kt")

        val expectedDimensOutput = """
            package com.org.newexample

            import androidx.compose.ui.unit.dp
            import androidx.compose.ui.unit.sp

            object NewExampleDimens {
                val fontSizeLarge = 18.sp
                val fontSizeSmall = 12.sp
                val lineHeightLarge = 24.sp
                val lineHeightSmall = 16.sp
                val letterSpacingNormal = 0.025.sp
                val letterSpacingLarge = 0.05.sp
            }
        """.trimIndent()

        val expectedColorsOutput = """
            package com.org.newexample

            import androidx.compose.ui.graphics.Color

            object NewExampleColors {
                val textColorPrimary = Color(0xFF000000)
                val textColorSecondary = Color(0xFF808080)
                val colorAccent = Color(0xFFFF4081)
                val colorPrimary = Color(0xFF3F51B5)
            }
        """.trimIndent()

        val expectedStylesOutput = """
            package com.org.newexample

            import androidx.compose.ui.text.font.FontWeight
            import androidx.compose.ui.text.TextStyle
            import com.org.newexample.NewExampleColors
            import com.org.newexample.NewExampleDimens

            object NewExampleStyles {
                val myStyle = TextStyle(
                    color = NewExampleColors.textColorPrimary,
                    fontSize = NewExampleDimens.fontSizeLarge,
                    fontWeight = FontWeight.Bold,
                    lineHeight = NewExampleDimens.lineHeightLarge,
                    letterSpacing = NewExampleDimens.letterSpacingNormal,
                )
            
                val anotherStyle = TextStyle(
                    color = NewExampleColors.textColorSecondary,
                    fontSize = NewExampleDimens.fontSizeSmall,
                    fontWeight = FontWeight.Italic,
                    lineHeight = NewExampleDimens.lineHeightSmall,
                    letterSpacing = NewExampleDimens.letterSpacingLarge,
                )
            
            }
        """.trimIndent()

//        Assertions.assertTrue(dimensOutputFile.exists())
//        Assertions.assertTrue(colorsOutputFile.exists())
//        Assertions.assertTrue(stylesOutputFile.exists())
        Assertions.assertEquals(expectedDimensOutput, dimensOutputFile.readText().trim())
        Assertions.assertEquals(expectedColorsOutput, colorsOutputFile.readText().trim())
        Assertions.assertEquals(expectedStylesOutput, stylesOutputFile.readText().trim())
    }
}
