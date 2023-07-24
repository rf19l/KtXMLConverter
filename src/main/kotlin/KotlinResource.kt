package com.rf.foster.ktxml

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