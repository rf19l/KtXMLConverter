package com.rf.foster.ktxml

class KotlinFileBuilder {
    private fun KotlinResource.getItemValue(): String {
        return when (this) {
            is KotlinColorResource -> this.value
            is KotlinDimenResource -> "${this.value}${this.unit}"
            is KotlinLiteralResource -> this.value
            is KotlinStyleResource -> ""
        }
    }

    fun buildStyles(packageName: String, projectName: String, styles: List<KotlinStyleResource>): String {
        val sb = StringBuilder()

        sb.appendLine("package $packageName")
        sb.appendLine()
        sb.appendLine("import androidx.compose.ui.text.font.FontWeight")
        sb.appendLine("import androidx.compose.ui.text.TextStyle")
        sb.appendLine("import $packageName.${projectName}Colors")
        sb.appendLine("import $packageName.${projectName}Dimens")
        sb.appendLine()
        sb.appendLine("object ${projectName}Styles {")

        styles.forEach { style ->
            sb.appendLine("    val ${style.name} = TextStyle(")
            style.items.forEach { item ->
                sb.appendLine("        ${item.name} = ${item.getItemValue()},")
            }
            sb.appendLine("    )")
            sb.appendLine()
        }

        sb.appendLine("}")

        return sb.toString()
    }
}
