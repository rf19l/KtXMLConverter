package io.github.rf19l.ktxml.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class ConvertResourceDirectoryTask : DefaultTask() {
    @TaskAction
    fun convertResources() {
        println("Running all XML conversion tasks")
    }
}