package com.rf.foster.ktxml.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

// This is your new task that runs all the tasks
abstract class ConvertResourceDirectoryTask : DefaultTask() {
    @TaskAction
    fun convertResources() {
        println("Running all XML conversion tasks")
    }
}