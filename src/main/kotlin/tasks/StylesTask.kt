package io.github.rf19l.ktxml.tasks

import io.github.rf19l.ktxml.mappers.KotlinFileBuilder
import io.github.rf19l.ktxml.mappers.RawXmlParser
import io.github.rf19l.ktxml.mappers.XmlResourceMapper
import io.github.rf19l.ktxml.models.KotlinStyleResource
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class StylesTask : DefaultTask() {
    @get:Input
    abstract val projectName: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputFile
    val outputFile: RegularFileProperty = project.objects.fileProperty().convention(
        project.layout.buildDirectory.file(
            project.provider {
                "generated/source/kapt/debug/${packageName.get().replace('.', '/')}/${projectName.get()}Styles.kt"
            }
        )
    )

    @TaskAction
    fun convertStylesToKotlin() {
        val parser = RawXmlParser()
        val stylesFile = File(project.projectDir, "src/main/res/values/styles.xml")
        if (stylesFile.exists().not()) {
            println("WARNING: ${stylesFile.absolutePath} does not exist.")
            return
        }
        val rawStyles = parser.parseXml(stylesFile)
        val mapper = XmlResourceMapper(projectName.get())
        val kotlinStyles = mapper.transformToKotlinResource(rawStyles).filterIsInstance<KotlinStyleResource>()

        // Ensure the output directory exists
        outputFile.get().asFile.parentFile.mkdirs()

        // Use the outputFile property directly to get the File instance
        outputFile.get().asFile.writeText(
            KotlinFileBuilder().buildStyles(
                packageName.get(),
                projectName.get(),
                kotlinStyles
            )
        )
    }
}
