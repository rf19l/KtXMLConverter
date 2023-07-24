package com.rf.foster.ktxml

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

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
        val parser = RawXmlParser()
        val dimensFile = File(project.projectDir, "src/main/res/values/dimens.xml")
        if (dimensFile.exists().not()) {
            println("WARNING: ${dimensFile.absolutePath} does not exist.")
            return
        }
        val rawDimens = parser.parseXml(dimensFile)
        val mapper = XmlResourceMapper(projectName.get())
        val kotlinDimensionResource = mapper.transformToKotlinResource(rawDimens).filterIsInstance<KotlinDimenResource>()
        val outputDir = File(project.buildDir, "generated/source/kapt/debug/${packageName.get().replace('.', '/')}")
        outputDir.mkdirs()
        println(kotlinDimensionResource.map { it.value }.toString())
        val outputFile = File(outputDir, "${projectName.get()}Dimens.kt")
        outputFile.writeText(KotlinFileBuilder().buildDimens(packageName.get(), projectName.get(), kotlinDimensionResource))
        /*        val rawXmlParser = RawXmlParser()
        val inputFile = project.file("src/main/res/values/dimens.xml")
        if (!inputFile.exists()) {
            println("WARNING: ${inputFile.absolutePath} does not exist.")
            return
        }
        val outputDir = File(project.buildDir, "generated/source/kapt/debug/${packageName.get().replace('.', '/')}")
        outputDir.mkdirs() // Ensure the directory exists
        val outputFile = File(outputDir, "${projectName.get()}Dimens.kt")
        val dimenXmlResources = rawXmlParser.parseXml(inputFile.readText())
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
        outputFile.writeText(stringBuilder.toString())*/
    }

}


abstract class ColorsTask : DefaultTask() {
    @get:Input
    abstract val projectName: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @TaskAction
    fun convertColors() {
        val parser = RawXmlParser()
        val colorsFile = File(project.projectDir, "src/main/res/values/colors.xml")
        if (colorsFile.exists().not()) {
            println("WARNING: ${colorsFile.absolutePath} does not exist.")
            return
        }
        val rawColors = parser.parseXml(colorsFile)
        val mapper = XmlResourceMapper(projectName.get())
        val kotlinColors = mapper.transformToKotlinResource(rawColors).filterIsInstance<KotlinColorResource>()
        val outputDir = File(project.buildDir, "generated/source/kapt/debug/${packageName.get().replace('.', '/')}")
        outputDir.mkdirs()
        println(kotlinColors.map { it.value }.toString())
        val outputFile = File(outputDir, "${projectName.get()}Colors.kt")
        outputFile.writeText(KotlinFileBuilder().buildColors(packageName.get(), projectName.get(), kotlinColors))
    }
}


abstract class StylesTask : DefaultTask() {
    @get:Input
    abstract val projectName: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @TaskAction
    fun convertStylesToKotlin() {
        val parser = RawXmlParser()
        val stylesFile = File(project.projectDir, "src/main/res/values/styles.xml")
        println("WARNING: ${stylesFile.absolutePath} does not exist.")
        val rawStyles = parser.parseXml(stylesFile)
        val mapper = XmlResourceMapper(projectName.get())
        val kotlinStyles = mapper.transformToKotlinResource(rawStyles).filterIsInstance<KotlinStyleResource>()
        val outputDir = File(project.buildDir, "generated/source/kapt/debug/${packageName.get().replace('.', '/')}")
        outputDir.mkdirs()
        val outputFile = File(outputDir, "${projectName.get()}Styles.kt")
        outputFile.writeText(KotlinFileBuilder().buildStyles(packageName.get(), projectName.get(), kotlinStyles))
    }
}





