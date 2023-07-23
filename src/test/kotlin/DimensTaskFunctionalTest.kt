import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class DimensTaskFunctionalTest {

    @TempDir
    lateinit var testProjectDir: File
    private lateinit var settingsFile: File
    private lateinit var buildFile: File
    private val packageName = "com.org.example"

    @BeforeEach
    fun setup() {
        settingsFile = testProjectDir.resolve("settings.gradle.kts")
        buildFile = testProjectDir.resolve("build.gradle.kts")
    }

    @Test
    fun `dimens task generates expected output`(@TempDir tempDir: Path) {
        // Setup test project
        val projectDir = tempDir.toFile()
        val settingsFile = File(projectDir, "settings.gradle.kts")
        val buildFile = File(projectDir, "build.gradle.kts")
        File(projectDir, "src/main/res/values")

        // Set project name
        val projectName = "Example"

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
                packageName.set("com.org.example")
            }
        """.trimIndent())
        // Write dimens.xml to the project directory
        val resDir = File(testProjectDir, "src/main/res/values")
        resDir.mkdirs()
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
                    <dimen name="unitless_margin">10</dimen>
                </resources>

            """.trimIndent()
        )

        // Run the dimens task
        GradleRunner.create().withProjectDir(testProjectDir).withArguments("konvertDimens", "--stacktrace")
            .withPluginClasspath().build()

        // Check the output
        val expectedOutput = """
    package $packageName

    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp

    object ${projectName}Dimens {
        val activityHorizontalMargin = 16.dp
        val activityVerticalMargin = 12.dp
        val appbarPadding = 8.dp
        val fabMargin = 16.dp
        val textSizeSmall = 12.sp
        val textSizeMedium = 16.sp
        val textSizeLarge = 20.sp
        val unitlessMargin = 10f
    }
""".trimIndent()

        val outputDir = File(testProjectDir, "build/generated/source/kapt/debug/${packageName.replace('.', '/')}")
        val outputFile = File(outputDir, "${projectName}Dimens.kt")
        Assertions.assertTrue(outputFile.exists())
        Assertions.assertEquals(expectedOutput, outputFile.readText().trim())
    }
    @Test
    fun `converts dimens names to camel case`() {
        // Set project name
        val projectName = "Example"

        // Set up the test project
        settingsFile.writeText("""
        rootProject.name = "$projectName"
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
        val resDir = File(testProjectDir, "src/main/res/values")
        resDir.mkdirs()
        val dimensFile = File(resDir, "dimens.xml")
        dimensFile.writeText("""
        <?xml version="1.0" encoding="utf-8"?>
        <resources>
            <dimen name="size_layout_01">16dp</dimen>
        </resources>
    """.trimIndent())

        // Run the dimens task
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("konvertDimens", "--stacktrace")
            .withPluginClasspath()
            .build()

        // Check the output
        val expectedName = "val sizeLayout01 = 16.dp"
        val outputDir = File(testProjectDir, "build/generated/source/kapt/debug/${packageName.replace('.', '/')}")
        val outputFile = File(outputDir, "${projectName}Dimens.kt")

        Assertions.assertTrue(outputFile.exists(), "Output file does not exist")

        val actualContent = outputFile.readText().trim()
        Assertions.assertEquals(
            expectedName,
            actualContent.substringAfterLast("object ${projectName}Dimens {")
                .substringBeforeLast("}")
                .trim(),
            "Output content does not match the expected content"
        )
    }

}


