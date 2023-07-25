import io.github.rf19l.ktxml.mappers.RawXmlParser
import io.github.rf19l.ktxml.models.ColorXmlResource
import io.github.rf19l.ktxml.models.DimenXmlResource
import io.github.rf19l.ktxml.models.StyleXmlResource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

    @Test
    fun parseStylesFile() {
        val stylesXml = """
            <resources>
                <style name="MyStyle" parent="TextAppearance.AppCompat">
                    <item name="android:textColor">@color/textColorPrimary</item>
                    <item name="android:textSize">@dimen/font_size_large</item>
                    <item name="android:textStyle">bold</item>
                    <item name="lineHeight">@dimen/line_height_large</item>
                    <item name="android:letterSpacing">@dimen/letter_spacing_normal</item>
                </style>
                <style name="AnotherStyle" parent="TextAppearance.Design.Tab">
                    <item name="android:textColor">@color/textColorSecondary</item>
                    <item name="android:textSize">@dimen/font_size_small</item>
                    <item name="android:textStyle">italic</item>
                    <item name="lineHeight">@dimen/line_height_small</item>
                    <item name="android:letterSpacing">@dimen/letter_spacing_large</item>
                </style>
            </resources>
            """
        val styles = rawXmlParser.parseXml(stylesXml)
        assertEquals(1, styles.size)
        val stylesFile = """
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

    }

    // Similar test for parseXml File...
    @Test
    fun `parseXml file`() {
        val tempDir = Files.createTempDirectory("test").toFile()
        val stylesFile = File(tempDir, "styles.xml").apply {
            writeText(
                """
    <resources>
        <style name="Common.Parent.Style" parent="TextAppearance.AppCompat">
            <item name="android:textColor" />
        </style>
        <style name="MyStyle" parent="Common.Parent.Style">
            <item name="android:textSize">@dimen/font_size_small</item>
            <item name="android:textStyle">normal</item>
            <item name="lineHeight">@dimen/line_height_one</item>
            <item name="android:letterSpacing">@dimen/letter_spacing_normal</item>
        </style>
    
        <style name="TabStyle" parent="TextAppearance.Design.Tab">
            <item name="android:textSize">@dimen/size_font_size_03</item>
            <item name="android:textStyle">bold</item>
            <item name="lineHeight">@dimen/calculated_line_height_default_paragraph_sm_strong</item>
            <item name="android:letterSpacing">@dimen/size_letter_spacing_normal</item>
            <item name="tabSelectedTextColor">@color/color_neutral_100</item>
            <item name="tabTextColor">@color/color_neutral_60</item>
            <item name="textAllCaps">false</item>
        </style>
    
    </resources>
        """
            )
        }
        val resources = rawXmlParser.parseXml(stylesFile)
        assertEquals(2, resources.size)
        val myStyleResource = resources.find { it is StyleXmlResource && it.name == "MyStyle" } as StyleXmlResource
        assertTrue(myStyleResource.items.any { it.name == "android:textSize" && it.ref == "@dimen/font_size_small" })
        tempDir.deleteRecursively()
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