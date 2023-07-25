package io.github.rf19l.ktxml.tasks

import io.github.rf19l.ktxml.models.KotlinColorResource
import io.github.rf19l.ktxml.mappers.KotlinFileBuilder
import io.github.rf19l.ktxml.mappers.RawXmlParser
import io.github.rf19l.ktxml.mappers.XmlResourceMapper
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

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
        val outputFile = File(outputDir, "${projectName.get()}Colors.kt")
        outputFile.writeText(KotlinFileBuilder().buildColors(packageName.get(), projectName.get(), kotlinColors))
    }
}