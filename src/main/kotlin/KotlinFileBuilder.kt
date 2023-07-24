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
    fun buildDimens(packageName: String, projectName: String, dimens: List<KotlinDimenResource>): String {
        val sb = StringBuilder()

        sb.appendLine("package $packageName")
        sb.appendLine()
        sb.appendLine("import androidx.compose.ui.unit.dp")
        sb.appendLine("import androidx.compose.ui.unit.sp")
        sb.appendLine()
        sb.appendLine("object ${projectName}Dimens {")

        dimens.forEach { dimen ->
            sb.appendLine("    val ${dimen.name} = ${dimen.getItemValue()}")
        }

        sb.appendLine("}")

        return sb.toString()
    }

    fun buildColors(packageName: String, projectName: String, colors: List<KotlinColorResource>): String {
        val sb = StringBuilder()
        sb.appendLine("package $packageName")
        sb.appendLine()
        sb.appendLine("import androidx.compose.ui.graphics.Color")
        sb.appendLine()
        sb.appendLine("object ${projectName}Colors {")

        colors.forEach { color ->
            sb.appendLine("    val ${color.name} = ${color.getItemValue()}")
        }

        sb.appendLine("}")

        return sb.toString().trimIndent()
    }
}
