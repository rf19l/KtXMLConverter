import com.rf.foster.ktxml.mappers.RawXmlParser
import com.rf.foster.ktxml.models.ColorXmlResource
import com.rf.foster.ktxml.models.DimenXmlResource
import com.rf.foster.ktxml.models.StyleXmlResource
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

class RawXmlParserTest {

    private val rawXmlParser = RawXmlParser()

    @Test
    fun `parseXml string`() {
        val stylesXml = """
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
        """

        val dimensXml = """
        <resources>
            <dimen name="font_size_large">18sp</dimen>
            <dimen name="font_size_small">12sp</dimen>
            <dimen name="line_height_large">24sp</dimen>
            <dimen name="line_height_small">16sp</dimen>
            <dimen name="letter_spacing_normal">0.025</dimen>
            <dimen name="letter_spacing_large">0.05</dimen>
        </resources>
        """

        val colorsXml = """
        <resources>
            <color name="textColorPrimary">#000000</color>
            <color name="textColorSecondary">#808080</color>
            <color name="colorAccent">#FF4081</color>
            <color name="colorPrimary">#3F51B5</color>
        </resources>
        """

        val styles = rawXmlParser.parseXml(stylesXml)
        val dimens = rawXmlParser.parseXml(dimensXml)
        val colors = rawXmlParser.parseXml(colorsXml)

        assertEquals(2, styles.size)
        assertEquals(6, dimens.size)
        assertEquals(4, colors.size)

        val myStyleResource = styles.find { it is StyleXmlResource && it.name == "MyStyle" } as StyleXmlResource
        val largeFontSizeDimenResource =
            dimens.find { it is DimenXmlResource && it.name == "font_size_large" } as DimenXmlResource
        val textColorPrimaryResource =
            colors.find { it is ColorXmlResource && it.name == "textColorPrimary" } as ColorXmlResource

        assertTrue(myStyleResource.items.any { it.name == "android:textSize" && it.ref == "@dimen/font_size_large" })
        assertEquals("18sp", largeFontSizeDimenResource.value)
        assertEquals("#000000", textColorPrimaryResource.hex)
    }

    // Similar test for parseXml File...
    @Test
    fun `parseXml file`() {
        val tempDir = Files.createTempDirectory("test").toFile()
        val stylesFile = File(tempDir, "styles.xml").apply {
            writeText(
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
        """
            )
        }

        val resources = rawXmlParser.parseXml(stylesFile)

        assertEquals(2, resources.size)

        val myStyleResource = resources.find { it is StyleXmlResource && it.name == "MyStyle" } as StyleXmlResource
        assertTrue(myStyleResource.items.any { it.name == "android:textSize" && it.ref == "@dimen/font_size_large" })

        tempDir.deleteRecursively()  // clean up temp directory
    }

    @Test
    fun parseDimensXmlFile() {
        val dimensXml = """
        <resources>
            <dimen name="font_size_large">18sp</dimen>
            <dimen name="font_size_small">12sp</dimen>
            <dimen name="line_height_large">24sp</dimen>
            <dimen name="line_height_small">16sp</dimen>
            <dimen name="letter_spacing_normal">0.025</dimen>
            <dimen name="letter_spacing_large">0.05</dimen>
        </resources>
        """
        val expectedDimensParsed = listOf(
            DimenXmlResource(name = "font_size_large", "18sp"),
            DimenXmlResource(name = "font_size_small", "12sp"),
            DimenXmlResource(name = "line_height_large", "24sp"),
            DimenXmlResource(name = "line_height_small", "16sp"),
            DimenXmlResource(name = "letter_spacing_normal", "0.025"),
            DimenXmlResource(name = "letter_spacing_large", "0.05"),
        )
        val tempDir = Files.createTempDirectory("test").toFile()
        val dimensFile = File(tempDir, "dimens.xml").apply { writeText(dimensXml) }

        val dimensResources = rawXmlParser.parseXml(dimensFile)

        assertEquals(expectedDimensParsed.size, dimensResources.size)
        expectedDimensParsed.zip(dimensResources.filterIsInstance<DimenXmlResource>()).forEach {
            val (expected, actual) = it
            assertEquals(expected.name, actual.name)
            assertEquals(expected.value, actual.value)
        }

        tempDir.deleteRecursively()  // clean up temp directory
    }
}