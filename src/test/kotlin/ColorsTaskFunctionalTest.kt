import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class ColorsTaskFunctionalTest {

    @Test
    fun `convert colors XML to Kotlin object`(@TempDir tempDir: Path) {
        // Setup test project
        val projectDir = tempDir.toFile()
        val settingsFile = File(projectDir, "settings.gradle.kts")
        val buildFile = File(projectDir, "build.gradle.kts")
        val srcDir = File(projectDir, "src/main/res/values")

        // Set project name
        val projectName = "Example"
        val packageName = "com.org.example"

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

        // Create source directory and color XML file
        srcDir.mkdirs()
        val colorsXmlFile = File(srcDir, "colors.xml")
        colorsXmlFile.writeText("""
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <color name="colorPrimary">#3F51B5</color>
                <color name="colorPrimaryDark">#303F9F</color>
                <color name="colorAccent">#FF4081</color>
                <color name="textColorPrimary">#212121</color>
                <color name="textColorSecondary">#757575</color>
                <color name="dividerColor">#BDBDBD</color>
            </resources>
        """.trimIndent())

        // Run the colors task
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("konvertColors")
            .withPluginClasspath()
            .build()

        // Check the output file
        val outputDir = File(projectDir, "build/generated/source/kapt/debug/com/org/example")
        val colorsOutputKt = File(outputDir, "${projectName}Colors.kt")

        val expectedOutput = """
    package $packageName

    import androidx.compose.ui.graphics.Color

    object ${projectName}Colors {
        val colorPrimary = Color(0xFF3F51B5)
        val colorPrimaryDark = Color(0xFF303F9F)
        val colorAccent = Color(0xFFFF4081)
        val textColorPrimary = Color(0xFF212121)
        val textColorSecondary = Color(0xFF757575)
        val dividerColor = Color(0xFFBDBDBD)
    }
""".trimIndent()

        val actualOutput = colorsOutputKt.readText().trim()

        assertEquals(expectedOutput, actualOutput)
    }
}
