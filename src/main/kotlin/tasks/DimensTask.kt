package io.github.rf19l.ktxml.tasks

import io.github.rf19l.ktxml.mappers.KotlinFileBuilder
import io.github.rf19l.ktxml.mappers.RawXmlParser
import io.github.rf19l.ktxml.mappers.XmlResourceMapper
import io.github.rf19l.ktxml.models.KotlinDimenResource
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
        val outputFile = File(outputDir, "${projectName.get()}Dimens.kt")
        outputFile.writeText(KotlinFileBuilder().buildDimens(packageName.get(), projectName.get(), kotlinDimensionResource))
    }

}