package io.github.rf19l.ktxml.models

sealed class XmlResource {
    abstract val name: String
}
data class DimenXmlResource(override val name: String, val value: String) : XmlResource()
data class ColorXmlResource(override val name: String, val hex: String) : XmlResource()
data class StyleXmlResource(override val name: String, val items: List<Item>,val parent: String? = null,) : XmlResource() {
    data class Item(val name: String, val ref: String)
}