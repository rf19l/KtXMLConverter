import com.rf.foster.ktxml.ParsedItem
import com.rf.foster.ktxml.XmlParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

/*

class XmlParserTest {

    private lateinit var xmlParser: XmlParser

    @BeforeEach
    fun setup() {
        xmlParser = XmlParser("MyProject")
    }
    private fun readXml(xmlContent:String): Document {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val xmlInput = InputSource(StringReader(xmlContent))
        val doc = dBuilder.parse(xmlInput)
        doc.documentElement.normalize()
        return doc
    }

    @Test
    fun `parseItem from colors xml should return Colors object`() {
        val xmlContent = """
        <resources>
            <color name="textColorPrimary">#000000</color>
            <color name="textColorSecondary">#808080</color>
            <color name="colorAccent">#FF4081</color>
            <color name="colorPrimary">#3F51B5</color>
        </resources>
    """.trimIndent()
        val doc = readXml(xmlContent)
        val nodeList = doc.getElementsByTagName("color")
        val node = nodeList.item(0) // get the first node
        val parsedItem = xmlParser.parseItem(node)

        assertTrue(parsedItem is ParsedItem.Colors)
        assertEquals("textColorPrimary", parsedItem.itemName)
        assertEquals("000000", parsedItem.itemValue)
        assertEquals("MyProjectColors", parsedItem.referencedClass)
    }


    @Test
    fun `parseItem from dimens xml should return Dimens object`() {
        val dimensXml = """
        <resources>
            <dimen name="font_size_large">18sp</dimen>
            <dimen name="font_size_small">12sp</dimen>
            <dimen name="line_height_large">24sp</dimen>
            <dimen name="line_height_small">16sp</dimen>
            <dimen name="letter_spacing_normal">0.025</dimen>
            <dimen name="letter_spacing_large">0.05</dimen>
        </resources>
    """.trimIndent()
        val doc = readXml(dimensXml)
        val nodeList = doc.getElementsByTagName("dimen")
        val node = nodeList.item(0) // get the first node
        val parsedItem = xmlParser.parseItem(node)

        assertTrue(parsedItem is ParsedItem.Dimens)
        assertEquals("font_size_large", parsedItem.itemName)
        assertEquals("18sp", parsedItem.itemValue)
        assertEquals("MyProjectDimens", parsedItem.referencedClass)
    }

    @Test
    fun `parseItem from styles xml should return Styles object`() {
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
    """.trimIndent()
        val doc = readXml(stylesXml)
        val nodeList = doc.getElementsByTagName("style")
        val node = nodeList.item(0) // get the first node
        val parsedItem = xmlParser.parseItem(node)
        assertTrue(parsedItem is ParsedItem.Styles)
        assertEquals("MyStyle", parsedItem.itemName)
        assertEquals("MyProjectStyles", parsedItem.referencedClass)
    }

}

*/
