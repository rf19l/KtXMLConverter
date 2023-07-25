import io.github.rf19l.ktxml.mappers.XmlResourceMapper
import io.github.rf19l.ktxml.models.ColorXmlResource
import io.github.rf19l.ktxml.models.DimenXmlResource
import io.github.rf19l.ktxml.models.StyleXmlResource
import io.github.rf19l.ktxml.models.*
import org.gradle.internal.impldep.junit.framework.TestCase.assertEquals
import org.gradle.internal.impldep.junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.Test

class ResourceMapperTest {
    @Test
    fun testXmlResourceMapper() {
        // assuming that rawXmlParser.parseXml has been called and the parsed lists are:
        val styles = listOf(
            StyleXmlResource(
                "MyStyle", listOf(
                    StyleXmlResource.Item("android:textColor", "@color/textColorPrimary"),
                    StyleXmlResource.Item("android:textSize", "@dimen/fontSizeLarge"),
                    StyleXmlResource.Item("android:textStyle", "bold"),
                    StyleXmlResource.Item("lineHeight", "@dimen/lineHeightLarge"),
                    StyleXmlResource.Item("android:letterSpacing", "@dimen/letterSpacingNormal"),
                )
            ), StyleXmlResource(
                "AnotherStyle", listOf(
                    StyleXmlResource.Item("android:textColor", "@color/textColorSecondary"),
                    StyleXmlResource.Item("android:textSize", "@dimen/fontSizeSmall"),
                    StyleXmlResource.Item("android:textStyle", "italic"),
                    StyleXmlResource.Item("lineHeight", "@dimen/lineHeightSmall"),
                    StyleXmlResource.Item("android:letterSpacing", "@dimen/letterSpacingLarge"),
                )
            )
        )
        val dimens = listOf(
            DimenXmlResource("fontSizeLarge", "18sp"),
            DimenXmlResource("fontSizeSmall", "12sp"),
            DimenXmlResource("lineHeightLarge", "24sp"),
            DimenXmlResource("lineHeightSmall", "16sp"),
            DimenXmlResource("letterSpacingNormal", "0.025"),
            DimenXmlResource("letterSpacingLarge", "0.05")
        )
        val colors = listOf(
            ColorXmlResource("textColorPrimary", "#000000"),
            ColorXmlResource("textColorSecondary", "#808080"),
            ColorXmlResource("colorAccent", "#FF4081"),
            ColorXmlResource("colorPrimary", "#3F51B5")
        )

        // instantiate our XmlResourceMapper with a project name
        val projectName = "ProjectName"
        val xmlResourceMapper = XmlResourceMapper(projectName)

        // map xml resources to kotlin resources
        val kotlinStyles = xmlResourceMapper.transformToKotlinResource(styles)
        val kotlinDimens = xmlResourceMapper.transformToKotlinResource(dimens)
        val kotlinColors = xmlResourceMapper.transformToKotlinResource(colors)

        // now let's check if the mapping is correct
        val myStyleResource =
            kotlinStyles.find { it is KotlinStyleResource && it.name == "myStyle" } as KotlinStyleResource
        val largeFontSizeDimenResource =
            kotlinDimens.find { it is KotlinDimenResource && it.name == "fontSizeLarge" } as KotlinDimenResource
        val textColorPrimaryResource =
            kotlinColors.find { it is KotlinColorResource && it.name == "textColorPrimary" } as KotlinColorResource

        assertTrue(myStyleResource.items.filterIsInstance<KotlinDimenResource>()
            .any { it.name == "fontSize" && it.value == "${projectName}Dimens.fontSizeLarge" })
        assertEquals("18", largeFontSizeDimenResource.value)
        assertEquals("Color(0xFF000000)", textColorPrimaryResource.value)

        val expectedKotlinStyles = listOf(
            KotlinStyleResource(
                "myStyle", listOf(
                    KotlinColorResource("color",value = "${projectName}Colors.textColorPrimary"),
                    KotlinDimenResource("fontSize", "${projectName}Dimens.fontSizeLarge", ""),
                    KotlinLiteralResource("fontWeight", "FontWeight.Bold"),
                    KotlinDimenResource("lineHeight", "${projectName}Dimens.lineHeightLarge", ""),
                    KotlinDimenResource("letterSpacing", "${projectName}Dimens.letterSpacingNormal", "")
                )
            ), KotlinStyleResource(
                "anotherStyle", listOf(
                    KotlinColorResource("color",value = "${projectName}Colors.textColorSecondary"),
                    KotlinDimenResource("fontSize", "${projectName}Dimens.fontSizeSmall",""),
                    KotlinLiteralResource("fontWeight", "FontWeight.Italic"),
                    KotlinDimenResource("lineHeight", "${projectName}Dimens.lineHeightSmall",""),
                    KotlinDimenResource("letterSpacing", "${projectName}Dimens.letterSpacingLarge", "")
                )
            )
        )
        assertEquals(expectedKotlinStyles.size, kotlinStyles.size)
        expectedKotlinStyles.forEachIndexed { index: Int, item: KotlinResource ->
            val expectedResource = item as KotlinStyleResource
            val mappedResource = kotlinStyles[index] as KotlinStyleResource
            assertEquals(expectedResource.name, mappedResource.name)
            assertEquals(expectedResource.items.size, mappedResource.items.size)

            // Compare each item inside the KotlinStyleResource
            expectedResource.items.zip(mappedResource.items).forEach { (expectedItem, mappedItem) ->
                when (expectedItem) {
                    is KotlinColorResource -> {
                        assert(mappedItem is KotlinColorResource)
                        assertEquals(expectedItem.name, (mappedItem as KotlinColorResource).name)
                        assertEquals(expectedItem.value, mappedItem.value)
                    }

                    is KotlinDimenResource -> {
                        assert(mappedItem is KotlinDimenResource)
                        val mappedDimen = mappedItem as KotlinDimenResource
                        assertEquals(expectedItem.name, mappedDimen.name)
                        assertEquals(expectedItem.value, mappedDimen.value)
                        assertEquals(expectedItem.unit, mappedDimen.unit)
                    }

                    is KotlinLiteralResource -> {
                        assert(mappedItem is KotlinLiteralResource)
                        assertEquals(expectedItem.name, (mappedItem as KotlinLiteralResource).name)
                        assertEquals(expectedItem.value, mappedItem.value)
                    }

                    else -> throw IllegalArgumentException("Unsupported item type: ${expectedItem::class.simpleName}")
                }
            }
        }
    }

    @Test
    fun testColorResourceMapper(){
        val colors = listOf(
            ColorXmlResource("textColorPrimary", "#000000"),
            ColorXmlResource("textColorSecondary", "#808080"),
            ColorXmlResource("colorAccent", "#FF4081"),
            ColorXmlResource("colorPrimary", "#3F51B5")
        )

        val projectName = "ProjectName"
        val xmlResourceMapper = XmlResourceMapper(projectName)
        val kotlinColors = xmlResourceMapper.transformToKotlinResource(colors)

        val expectedColors = listOf(
            KotlinColorResource("textColorPrimary","Color(0xFF000000)"),
            KotlinColorResource("textColorSecondary", "Color(0xFF808080)"),
            KotlinColorResource("colorAccent", "Color(0xFFFF4081)"),
            KotlinColorResource("colorPrimary", "Color(0xFF3F51B5)")
        )

        assertEquals(expectedColors.size, kotlinColors.size)
        expectedColors.zip(kotlinColors).forEach { (expected, actual) ->
            assertTrue(actual is KotlinColorResource)
            assertEquals(expected.name, (actual as KotlinColorResource).name)
            assertEquals(expected.value, actual.value)
        }
    }

    @Test
    fun testDimensResourceMapper(){
        val dimens = listOf(
            DimenXmlResource("font_size_large", "18sp"),
            DimenXmlResource("font_size_small", "12sp"),
            DimenXmlResource("line_height_large", "24sp"),
            DimenXmlResource("line_height_small", "16sp"),
            DimenXmlResource("letter_spacing_normal", "0.025"),
            DimenXmlResource("letter_spacing_large", "0.05"),
            DimenXmlResource("size_letter_spacing_normal","0")
        )

        val projectName = "ProjectName"
        val xmlResourceMapper = XmlResourceMapper(projectName)
        val kotlinDimens = xmlResourceMapper.transformToKotlinResource(dimens)

        val expectedDimens = listOf(
            KotlinDimenResource("fontSizeLarge", "18",".sp"),
            KotlinDimenResource("fontSizeSmall", "12",".sp"),
            KotlinDimenResource("lineHeightLarge", "24",".sp"),
            KotlinDimenResource("lineHeightSmall", "16",".sp"),
            KotlinDimenResource("letterSpacingNormal", "0.025",".sp"),
            KotlinDimenResource("letterSpacingLarge", "0.05",".sp"),
            KotlinDimenResource("sizeLetterSpacingNormal","0",".sp"),
        )

        assertEquals(expectedDimens.size, kotlinDimens.size)
        expectedDimens.zip(kotlinDimens).forEach { (expected, actual) ->
            assertTrue(actual is KotlinDimenResource)
            assertEquals(expected.name, (actual as KotlinDimenResource).name)
            assertEquals(expected.value, actual.value)
            assertEquals(expected.unit, actual.unit)
        }
    }

}