package com.rf.foster.ktxml.mappers

import com.rf.foster.ktxml.models.kotlin_resource.*
import com.rf.foster.ktxml.models.raw_xml.ColorXmlResource
import com.rf.foster.ktxml.models.raw_xml.DimenXmlResource
import com.rf.foster.ktxml.models.raw_xml.StyleXmlResource
import com.rf.foster.ktxml.models.raw_xml.XmlResource
import com.rf.foster.ktxml.utils.toCamelCase

class XmlResourceMapper(private val projectName: String) {

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


    private fun transformColor(color: ColorXmlResource): KotlinColorResource {
        val colorString = color.hex.removePrefix("#")
        val hexValue = when (colorString.length) {
            6 -> "0xFF${colorString}" // if the color code doesn't contain an alpha channel, we add it.
            8 -> "0x${colorString}" // if it contains an alpha channel, we just prepend "0x".
            else -> throw IllegalArgumentException("Invalid color code: ${color.hex}")
        }
        return KotlinColorResource(color.name.toCamelCase(), "Color($hexValue)")
    }

    private fun transformDimen(dimen: DimenXmlResource): KotlinDimenResource {
        val (value, unit) = when {
            dimen.value.endsWith("sp") -> Pair(dimen.value.removeSuffix("sp"), ".sp")
            dimen.value.endsWith("dp") -> Pair(dimen.value.removeSuffix("dp"), ".dp")
            else -> Pair(dimen.value, "f")
        }
        return KotlinDimenResource(dimen.name.toCamelCase(), value, unit)
    }

}