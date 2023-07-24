package com.rf.foster.ktxml.tasks

/*
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import java.io.File

abstract class BaseResourceTask : DefaultTask() {
    @get:Input
    abstract val projectName: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    protected fun getResourceFile(fileName: String): File {
        val file = File(project.projectDir, "src/main/res/values/$fileName.xml")
        if (!file.exists()) {
            println("WARNING: ${file.absolutePath} does not exist.")
        }
        return file
    }

    @Internal
    protected fun getOutputDir(): File {
        val outputDir = File(project.buildDir, "generated/source/kapt/debug/${packageName.get().replace('.', '/')}")
        outputDir.mkdirs()
        return outputDir
    }
}
*/
