package com.rf.foster.ktxml

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

class RawXmlParser {
    fun parseXml(file: File): List<XmlResource> {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(file)

        return parseXmlDocument(doc)
    }

    fun parseXml(xmlString: String): List<XmlResource> {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val inputSource = org.xml.sax.InputSource(StringReader(xmlString))
        val doc = dBuilder.parse(inputSource)

        return parseXmlDocument(doc)
    }

    private fun parseXmlDocument(doc: Document): List<XmlResource> {
        val list = mutableListOf<XmlResource>()

        doc.documentElement.normalize()

        val styleNodes = doc.getElementsByTagName("style")
        val colorNodes = doc.getElementsByTagName("color")
        val dimenNodes = doc.getElementsByTagName("dimen")

        for (i in 0 until styleNodes.length) {
            val node = styleNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element = node as Element
                val style = parseStyle(element)
                list.add(style)
            }
        }

        for (i in 0 until colorNodes.length) {
            val node = colorNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element = node as Element
                val color = parseColor(element)
                list.add(color)
            }
        }

        for (i in 0 until dimenNodes.length) {
            val node = dimenNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element = node as Element
                val dimen = parseDimen(element)
                list.add(dimen)
            }
        }

        return list
    }

    private fun parseStyle(node: Element): StyleXmlResource {
        val name = node.getAttribute("name")
        val items = node.getElementsByTagName("item")
        val itemList = mutableListOf<StyleXmlResource.Item>()

        for (i in 0 until items.length) {
            val itemNode = items.item(i)
            if (itemNode.nodeType == Node.ELEMENT_NODE) {
                val itemElement = itemNode as Element
                val itemName = itemElement.getAttribute("name")
                val itemRef = itemElement.textContent
                val item = StyleXmlResource.Item(itemName, itemRef)
                itemList.add(item)
            }
        }

        return StyleXmlResource(name, itemList)
    }

    private fun parseColor(node: Element): ColorXmlResource {
        val name = node.getAttribute("name")
        val hex = node.textContent

        return ColorXmlResource(name, hex)
    }

    private fun parseDimen(node: Element): DimenXmlResource {
        val name = node.getAttribute("name")
        val value = node.textContent

        return DimenXmlResource(name, value)
    }
}

sealed class XmlResource {
    abstract val name: String
}

data class DimenXmlResource(override val name: String, val value: String) : XmlResource()

data class ColorXmlResource(override val name: String, val hex: String) : XmlResource()

data class StyleXmlResource(override val name: String, val items: List<Item>) : XmlResource() {
    data class Item(val name: String, val ref: String)
}

//should be a string representation that we can write to a file to build a kotlin object
sealed class KotlinResource {
    abstract val name: String
}

/**
 * Represents import androidx.compose.ui.graphics.Color
 * When written to the kotlin file the format will be val name:Color(value)
 *
 */
data class KotlinColorResource(override val name: String, val value: String) : KotlinResource()

/**
 * Represents a dp,sp, or float
 * When written to the kotlin file the format will be val name:unit = value
 */
data class KotlinDimenResource(override val name: String, val value: String, val unit: String) : KotlinResource()

/**
 * Represents import androidx.compose.ui.text.TextStyle
 * When written to kotlin file the format will be val name = TextStyle(item.name = item.value) // comma separated values for each item in the list
 */
data class KotlinStyleResource(override val name: String, val items: List<KotlinResource>) : KotlinResource()

/**
 * Represents a raw string literal that's not a reference to any other resource.
 * When written to the kotlin file the format will be as raw string literal.
 */
data class KotlinLiteralResource(override val name: String, val value: String) : KotlinResource()


class XmlResourceMapper(private val projectName: String) {
    private fun String.transformNameIfInMap(): String {
        val xmlToComposeTextStyleMap = mapOf(
            ("textSize" to "fontSize"), ("textStyle" to "fontWeight")
        )
        return xmlToComposeTextStyleMap[this] ?: this
    }

    fun transformToKotlinResource(rawResources: List<XmlResource>): List<KotlinResource> {
        return when (rawResources.firstOrNull()) {
            is StyleXmlResource -> rawResources.map { it as StyleXmlResource }.map(::transformStyle)
            is ColorXmlResource -> rawResources.map { it as ColorXmlResource }.map(::transformColor)
            is DimenXmlResource -> rawResources.map { it as DimenXmlResource }.map(::transformDimen)
            else -> emptyList()
        }
    }

    private fun transformStyle(style: StyleXmlResource): KotlinStyleResource {
        val nameMap: Map<String, String?> = mapOf(
            "android:textSize" to "fontSize",
            "android:textStyle" to "fontWeight",
            "android:textColor" to "color"
        )
        val resourceTransforms = linkedMapOf("@color/" to { item: StyleXmlResource.Item ->
            val colorName = item.ref.removePrefix("@color/")
            KotlinColorResource(item.name.toCamelCase(), "${projectName}Colors.${colorName.toCamelCase()}")
        }, "@dimen/" to { item: StyleXmlResource.Item ->
            val dimenName = item.ref.removePrefix("@dimen/")
            KotlinDimenResource(item.name.toCamelCase(), "${projectName}Dimens.${dimenName.toCamelCase()}", "")
        })

        val items = style.items.mapNotNull { item ->
            if (nameMap.containsKey(item.name)) {
                if (nameMap[item.name] != null) {
                    val newName = nameMap[item.name]!!.toCamelCase()
                    val transform = resourceTransforms.entries.find { item.ref.startsWith(it.key) }?.value
                    if (transform != null) {
                        transform(item.copy(name = newName))
                    } else if (item.ref in listOf("bold", "italic", "normal")) { // handling textStyle
                        KotlinLiteralResource(newName, "FontWeight.${item.ref.capitalize()}")
                    } else {
                        println("WARNING: Unsupported reference: ${item.ref}")
                        null
                    }
                } else {
                    null
                }
            } else {
                val transform = resourceTransforms.entries.find { item.ref.startsWith(it.key) }?.value
                if (transform != null) {
                    transform(item)
                } else if (item.ref in listOf("bold", "italic", "normal")) { // handling textStyle
                    KotlinLiteralResource(item.name.toCamelCase(), item.ref)
                } else {
                    println("WARNING: Unsupported reference: ${item.ref}")
                    null
                }
            }
        }

        return KotlinStyleResource(style.name.toCamelCase(), items)
    }


    private fun transformColor(color: ColorXmlResource, isStyleChild: Boolean = false): KotlinColorResource {
        val colorString = color.hex.removePrefix("#")
        val hexValue = when (colorString.length) {
            6 -> "0xFF${colorString}" // if the color code doesn't contain an alpha channel, we add it.
            8 -> "0x${colorString}" // if it contains an alpha channel, we just prepend "0x".
            else -> throw IllegalArgumentException("Invalid color code: ${color.hex}")
        }
        return KotlinColorResource(color.name.toCamelCase(), "Color($hexValue)")
    }

    private fun transformDimen(dimen: DimenXmlResource, isStyleChild: Boolean = false): KotlinDimenResource {
        val (value, unit) = when {
            dimen.value.endsWith("sp") -> Pair(dimen.value.removeSuffix("sp"), ".sp")
            dimen.value.endsWith("dp") -> Pair(dimen.value.removeSuffix("dp"), ".dp")
            else -> Pair(dimen.value, "f")
        }
        return KotlinDimenResource(dimen.name.toCamelCase(), value, unit)
    }

}

