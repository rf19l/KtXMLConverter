package com.rf.foster.ktxml

import org.w3c.dom.Node

sealed class ParsedItem {
    abstract val itemName: String
    abstract val itemValue: String
    abstract val referencedClass: String
    open val projectName: String? = null
    abstract fun formatItemValue(): String

    data class Dimens(
        override val itemName: String,
        override val itemValue: String,
        override val referencedClass: String,
    ) : ParsedItem() {
        override fun formatItemValue(): String {
            val value = itemValue.replace("dp|sp".toRegex(), "")
            val unit = when {
                itemValue.endsWith("dp") -> ".dp"
                itemValue.endsWith("sp") -> ".sp"
                else -> "f"
            }
            return "$value$unit"
        }
    }

    data class Colors(
        override val itemName: String,
        override val itemValue: String,
        override val referencedClass: String,
    ) : ParsedItem() {
        override fun formatItemValue(): String {
            val value = if (itemValue.length == 6) "0xFF$itemValue" else "0x$itemValue"
            return "Color($value)"
        }
    }


    data class Styles(
        override val itemName: String,
        override val itemValue: String,
        override val referencedClass: String,
        override val projectName: String,
    ) : ParsedItem() {
        override fun formatItemValue(): String {
            return if (referencedClass.endsWith("Dimens")) {
                "$referencedClass.${itemValue.toCamelCase()}"
            } else {
                "$referencedClass.${itemValue.toCamelCase()}"
            }
        }
    }
}


class XmlParser(private val projectName: String = "") {
    fun parseItem(itemNode: Node): ParsedItem {
        val itemName = itemNode.attributes.getNamedItem("name").nodeValue.toCamelCase()
        var itemValue = itemNode.textContent.replace("@dimen/", "").replace("@color/", "").toCamelCase()
        var referencedClass = ""

        when (itemNode.nodeName) {
            "color" -> {
                itemValue =
                    itemValue.replace("#", "").toUpperCase() // Remove '#' from color value and convert to uppercase
                referencedClass = "${projectName}Colors"
            }

            "dimen" -> referencedClass = "${projectName}Dimens"
            "style" -> referencedClass = "${projectName}Styles" // Add this line
            "item" -> {
                referencedClass = when {
                    itemValue.contains("Dimen") -> "${projectName}Dimens"
                    itemValue.contains("Color") -> "${projectName}Colors"
                    else -> ""
                }
            }
        }

        return when (referencedClass) {
            "${projectName}Colors" -> ParsedItem.Colors(itemName, itemValue, referencedClass)
            "${projectName}Dimens" -> ParsedItem.Dimens(itemName, itemValue, referencedClass)
            "${projectName}Styles" -> ParsedItem.Styles(
                itemName,
                itemValue,
                referencedClass,
                projectName
            ) // Make sure this condition is covered
            else -> ParsedItem.Styles(itemName, itemValue, referencedClass, projectName)
        }
    }
}

