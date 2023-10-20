package io.github.rf19l.ktxml

import io.github.rf19l.ktxml.tasks.ColorsTask
import io.github.rf19l.ktxml.tasks.ConvertResourceDirectoryTask
import io.github.rf19l.ktxml.tasks.DimensTask
import io.github.rf19l.ktxml.tasks.StylesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

open class KtXMLConverterExtension(objects: ObjectFactory) {
    val projectName: Property<String> = objects.property(String::class.java)
    val packageName: Property<String> = objects.property(String::class.java)
}


class KtXMLConverter : Plugin<Project> {
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

        project.afterEvaluate {
            val preBuildTask = tasks.findByName("preBuild")
            preBuildTask?.dependsOn("konvertXmlResources")
        }
    }
}





