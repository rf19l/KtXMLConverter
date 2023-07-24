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
        val rawXmlParser = RawXmlParser()
        val inputFile = project.file("src/main/res/values/dimens.xml")
        if (!inputFile.exists()) {
            println("WARNING: ${inputFile.absolutePath} does not exist.")
            return
        }
        val outputDir = File(project.buildDir, "generated/source/kapt/debug/${packageName.get().replace('.', '/')}")
        outputDir.mkdirs() // Ensure the directory exists
        val outputFile = File(outputDir, "${projectName.get()}Dimens.kt")

        // Parse the XML input
        val dimenXmlResources = rawXmlParser.parseXml(inputFile.readText())

        // Map XML resources to Kotlin resources
        val xmlResourceMapper = XmlResourceMapper(projectName.get())
        val kotlinDimens =
            xmlResourceMapper.transformToKotlinResource(dimenXmlResources).filterIsInstance<KotlinDimenResource>()

        val stringBuilder = StringBuilder()

        stringBuilder.append("package ${packageName.get()}\n\n")
        stringBuilder.append("import androidx.compose.ui.unit.dp\n")
        stringBuilder.append("import androidx.compose.ui.unit.sp\n\n")
        stringBuilder.append("object ${projectName.get()}Dimens {\n")

        kotlinDimens.forEach {
            stringBuilder.append("    val ${it.name} = ${it.value}${it.unit}\n")
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

        // Use RawXmlParser to parse the XML
        val rawXmlParser = RawXmlParser()
        val xmlColors = rawXmlParser.parseXml(inputFile)

        // Transform XML resources to Kotlin resources using XmlResourceMapper
        val xmlResourceMapper = XmlResourceMapper(projectName.get())
        val kotlinColors =
            xmlResourceMapper.transformToKotlinResource(xmlColors).filterIsInstance<KotlinColorResource>()

        // Replace '.' with the file separator for the package name to create a file path
        val outputDir = File(project.buildDir, "generated/source/kapt/debug/${packageName.get().replace('.', '/')}")
        outputDir.mkdirs() // Ensure the directory exists
        // Use projectName in the output file name
        val outputFile = File(outputDir, "${projectName.get()}Colors.kt")

        val stringBuilder = StringBuilder()

        // Use packageName for the package declaration
        stringBuilder.append("package ${packageName.get()}\n\n")
        stringBuilder.append("import androidx.compose.ui.graphics.Color\n\n")
        // Use projectName in the object name
        stringBuilder.append("object ${projectName.get()}Colors {\n")

        for (kotlinColor in kotlinColors) {
            // Just directly use KotlinColorResource to generate the required line
            stringBuilder.append("    val ${kotlinColor.name} = ${kotlinColor.value}\n")
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
        // Step 1: Parse raw XML
        val parser = RawXmlParser()
        val stylesFile = File(project.projectDir, "src/main/res/values/styles.xml")
        val rawStyles = parser.parseXml(stylesFile)

        // Step 2: Transform raw XML to Kotlin
        val mapper = XmlResourceMapper(projectName.get())
        val kotlinStyles = mapper.transformToKotlinResource(rawStyles).filterIsInstance<KotlinStyleResource>()
        // Step 3: Write transformed data to file
        val outputDir = File(project.buildDir, "generated/source/kapt/debug/${packageName.get().replace('.', '/')}")
        outputDir.mkdirs()
        val outputFile = File(outputDir, "${projectName.get()}Styles.kt")
        outputFile.writeText(KotlinFileBuilder().buildStyles(packageName.get(), projectName.get(), kotlinStyles))
    }
}





