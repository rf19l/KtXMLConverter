package io.github.rf19l.ktxml.tasks

import io.github.rf19l.ktxml.mappers.KotlinFileBuilder
import io.github.rf19l.ktxml.models.KotlinStyleResource
import io.github.rf19l.ktxml.mappers.RawXmlParser
import io.github.rf19l.ktxml.mappers.XmlResourceMapper
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

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