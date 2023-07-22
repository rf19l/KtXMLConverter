package com.rf.foster.ktxml

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

open class KtXMLConverterExtension(objects: ObjectFactory) {
    val projectName: Property<String> = objects.property(String::class.java)
    val packageName: Property<String> = objects.property(String::class.java)
}


// This is your main plugin class
class ktXMLConverter : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(
            "ktXMLConverterExtension", KtXMLConverterExtension::class.java, project.objects
        )

        val dimensTask = project.tasks.create("konvertDimens", DimensTask::class.java) {
            this.projectName.set(extension.projectName)
            this.packageName.set(extension.packageName)
        }
        val colorsTask = project.tasks.create("konvertColors", ColorsTask::class.java) {
            this.projectName.set(extension.projectName)
            this.packageName.set(extension.packageName)
        }
        val stylesTask = project.tasks.create("konvertStyles", StylesTask::class.java) {
            this.projectName.set(extension.projectName)
            this.packageName.set(extension.packageName)
        }

        val allTasks = project.tasks.register("konvertXmlResources", ConvertResourceDirectoryTask::class.java) {
            this.dependsOn(dimensTask, colorsTask, stylesTask)
        }
    }
}

// This is your new task that runs all the tasks
abstract class ConvertResourceDirectoryTask : DefaultTask() {
    @TaskAction
    fun convertResources() {
        println("Running all XML conversion tasks")
    }
}


abstract class DimensTask : DefaultTask() {
    @get:Input
    abstract val projectName: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @TaskAction
    fun convertDimensToKotlin() {
        val inputFile = project.file("src/main/res/values/dimens.xml")
        if (!inputFile.exists()) {
            println("WARNING: ${inputFile.absolutePath} does not exist.")
            return
        }
        val outputDir = File(project.buildDir, "generated/source/kapt/debug/${packageName.get().replace('.', '/')}")
        outputDir.mkdirs() // Ensure the directory exists
        val outputFile = File(outputDir, "${projectName.get()}Dimens.kt")
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(inputFile)

        doc.documentElement.normalize()
        val nodeList = doc.getElementsByTagName("dimen")
        val stringBuilder = StringBuilder()

        stringBuilder.append("package ${packageName.get()}\n\n")
        stringBuilder.append("import androidx.compose.ui.unit.dp\n")
        stringBuilder.append("import androidx.compose.ui.unit.sp\n\n")
        stringBuilder.append("object ${projectName.get()}Dimens {\n")

        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element = node as Element
                val name =
                    element.getAttribute("name").replace("_([a-z])".toRegex()) { it.value[1].toUpperCase().toString() }
                val valueAndUnit = element.textContent
                val value = valueAndUnit.replace("dp|sp".toRegex(), "")
                val unit = when {
                    valueAndUnit.endsWith("dp") -> ".dp"
                    valueAndUnit.endsWith("sp") -> ".sp"
                    else -> "f"
                }

                stringBuilder.append("    val $name = $value$unit\n")
            }
        }

        stringBuilder.append("}\n")
        outputFile.writeText(stringBuilder.toString())
    }
}

abstract class ColorsTask : DefaultTask() {
    @get:Input
    abstract val projectName: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @TaskAction
    fun convertColors() {
        val inputFile = File(project.projectDir, "src/main/res/values/colors.xml")
        if (!inputFile.exists()) {
            println("WARNING: ${inputFile.absolutePath} does not exist.")
            return
        }
        // Replace '.' with the file separator for the package name to create a file path
        val outputDir = File(project.buildDir, "generated/source/kapt/debug/${packageName.get().replace('.', '/')}")
        outputDir.mkdirs() // Ensure the directory exists
        // Use projectName in the output file name
        val outputFile = File(outputDir, "${projectName.get()}Colors.kt")
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(inputFile)

        doc.documentElement.normalize()
        val nodeList = doc.getElementsByTagName("color")
        val stringBuilder = StringBuilder()

        // Use packageName for the package declaration
        stringBuilder.append("package ${packageName.get()}\n\n")
        stringBuilder.append("import androidx.compose.ui.graphics.Color\n\n")
        // Use projectName in the object name
        stringBuilder.append("object ${projectName.get()}Colors {\n")

        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element = node as Element
                val name = element.getAttribute("name").replace(Regex("_\\w")) { it.value[1].toUpperCase().toString() }
                var value = "0x" + element.textContent.substring(1)
                while (value.length < 10) {
                    value = "0x" + "F" + value.substring(2)
                }
                stringBuilder.append("    val $name = Color($value)\n")
            }
        }

        stringBuilder.append("}\n")
        outputFile.writeText(stringBuilder.toString())
    }

}

abstract class StylesTask : DefaultTask() {
    @get:Input
    abstract val projectName: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @TaskAction
    fun convertStylesToKotlin() {
        val inputFile = project.file("src/main/res/values/styles.xml")
        if (!inputFile.exists()) {
            println("WARNING: ${inputFile.absolutePath} does not exist.")
            return
        }
        val outputDir = project.file("build/generated/source/kapt/debug/${packageName.get().replace('.', '/')}")
        outputDir.mkdirs() // Ensure the directory exists
        val outputFile = File(outputDir, "${projectName.get()}Styles.kt")
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(inputFile)

        doc.documentElement.normalize()
        val nodeList = doc.getElementsByTagName("style")
        val stringBuilder = StringBuilder()

        stringBuilder.append("package ${packageName.get()}\n\n")
        stringBuilder.append("import androidx.compose.ui.text.TextStyle\n")
        stringBuilder.append("import ${packageName.get()}.${projectName.get()}Colors\n")
        stringBuilder.append("import ${packageName.get()}.${projectName.get()}Dimens\n\n")

        var fontWeightImportNeeded = false

        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element = node as Element

                // Parse each item
                val items = element.getElementsByTagName("item")
                val properties = mutableMapOf<String, String>()
                for (j in 0 until items.length) {
                    val item = items.item(j)
                    val itemName = item.attributes.getNamedItem("name").nodeValue
                    if (itemName == "android:textStyle") {
                        fontWeightImportNeeded = true
                    }
                }
            }
        }

        if (fontWeightImportNeeded) {
            stringBuilder.append("import androidx.compose.ui.text.font.FontWeight\n\n")
        }

        stringBuilder.append("object ${projectName.get()}Styles {\n")

        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element = node as Element
                val name =
                    element.getAttribute("name").replace(".", "_").split("_").joinToString("") { it.capitalize() }
                        .decapitalize()

                // Parse each item
                val items = element.getElementsByTagName("item")
                val properties = mutableMapOf<String, String>()
                for (j in 0 until items.length) {
                    val item = items.item(j)
                    val itemName = item.attributes.getNamedItem("name").nodeValue
                    val itemValue = item.textContent.replace("@dimen/", "").replace("@color/", "")
                        .replace("_([a-z0-9])".toRegex()) { it.groupValues[1].toUpperCase() }.decapitalize()
                    properties[itemName] = itemValue
                }

                val textSize = properties["android:textSize"]?.let { "${projectName.get()}Dimens.$it" }
                val textStyle = properties["android:textStyle"]
                val textColor = properties["android:textColor"]?.let { "${projectName.get()}Colors.$it" }
                val lineHeight = properties["lineHeight"]?.let { "${projectName.get()}Dimens.$it" }
                val letterSpacing = properties["android:letterSpacing"]?.let { "${projectName.get()}Dimens.$it" }

                stringBuilder.append("    val $name = TextStyle(\n")
                if (textSize != null) stringBuilder.append("        fontSize = $textSize,\n")
                if (textStyle != null) stringBuilder.append("        fontWeight = FontWeight.${textStyle.capitalize()},\n")
                if (textColor != null) stringBuilder.append("        color = $textColor,\n")
                if (lineHeight != null) stringBuilder.append("        lineHeight = $lineHeight,\n")
                if (letterSpacing != null) stringBuilder.append("        letterSpacing = $letterSpacing,\n")
                stringBuilder.append("    )\n")
            }
        }

        stringBuilder.append("}\n")
        outputFile.writeText(stringBuilder.toString())
    }
}




