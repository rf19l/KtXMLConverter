package io.github.rf19l.ktxml.mappers

import io.github.rf19l.ktxml.models.ColorXmlResource
import io.github.rf19l.ktxml.models.DimenXmlResource
import io.github.rf19l.ktxml.models.StyleXmlResource
import io.github.rf19l.ktxml.models.XmlResource
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
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
        val parsedList = mutableListOf<XmlResource>()

        doc.documentElement.normalize()

        val styleNodes = doc.getElementsByTagName("style")
        val colorNodes = doc.getElementsByTagName("color")
        val dimenNodes = doc.getElementsByTagName("dimen")

        parseStyles(styleNodes, parsedList)

        for (i in 0 until colorNodes.length) {
            val node = colorNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element = node as Element
                val color = parseColor(element)
                parsedList.add(color)
            }
        }

        for (i in 0 until dimenNodes.length) {
            val node = dimenNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element = node as Element
                val dimen = parseDimen(element)
                parsedList.add(dimen)
            }
        }

        return parsedList
    }

    private fun parseStyles(styleNodes: NodeList, parsedList: MutableList<XmlResource>) {
        val parentSet = mutableSetOf("TextAppearance.AppCompat")  // keep track of the allowed ancestors
        val hierarchyMap = mutableMapOf<String, String>() // map each style to its parent

        for (i in 0 until styleNodes.length) {
            val node = styleNodes.item(i) as Element
            if(node.nodeType == Node.ELEMENT_NODE){
                processNode(node, parsedList, parentSet, hierarchyMap)
            }
        }
    }

    private fun processNode(node: Element, parsedList: MutableList<XmlResource>, parentSet: MutableSet<String>, hierarchyMap: MutableMap<String, String>){
        val name = node.getAttribute("name")
        val parent = node.getAttribute("parent").takeIf { it.isNotBlank() }

        if(isDescendantOfTextStyle(parent, hierarchyMap)) {
            parent?.let {
                hierarchyMap[name] = it
                parentSet.add(it)
            }
            parsedList.add(parseStyle(node))
        }
    }

    private fun isDescendantOfTextStyle(
        parent: String?,
        hierarchy: Map<String, String>,
    ): Boolean {
        if (parent == null || parent == "TextAppearance.AppCompat") return true
        var currentParent = parent
        while (currentParent in hierarchy) {
            currentParent = hierarchy[currentParent] ?: break
        }
        return currentParent == "TextAppearance.AppCompat"
    }


    private fun parseStyle(node: Element): StyleXmlResource {
        val name = node.getAttribute("name")
        val parent = node.getAttribute("parent").takeIf { it.isNotBlank() }
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

        return StyleXmlResource(name = name, parent = parent, items = itemList)
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


