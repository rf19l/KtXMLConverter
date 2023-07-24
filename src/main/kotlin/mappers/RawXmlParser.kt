package com.rf.foster.ktxml.mappers

import com.rf.foster.ktxml.models.ColorXmlResource
import com.rf.foster.ktxml.models.DimenXmlResource
import com.rf.foster.ktxml.models.StyleXmlResource
import com.rf.foster.ktxml.models.XmlResource
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


