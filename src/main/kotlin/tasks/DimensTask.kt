package com.rf.foster.ktxml.tasks

import com.rf.foster.ktxml.models.kotlin_resource.KotlinDimenResource
import com.rf.foster.ktxml.mappers.KotlinFileBuilder
import com.rf.foster.ktxml.mappers.RawXmlParser
import com.rf.foster.ktxml.mappers.XmlResourceMapper
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

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