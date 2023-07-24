package com.rf.foster.ktxml.models.raw_xml

sealed class XmlResource {
    abstract val name: String
}
data class DimenXmlResource(override val name: String, val value: String) : XmlResource()
data class ColorXmlResource(override val name: String, val hex: String) : XmlResource()
data class StyleXmlResource(override val name: String, val items: List<Item>) : XmlResource() {
    data class Item(val name: String, val ref: String)
}