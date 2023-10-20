import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.PrintWriter
plugins {
    kotlin("jvm") version "1.9.0"
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.2.0"

}

group = "io.github.rf19l.ktxml"
version ="1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    testImplementation(gradleTestKit())
}
gradlePlugin {
    website.set("https://github.com/rf19l/ktxmlconverter")
    vcsUrl.set("https://github.com/rf19l/ktxmlconverter.git")
    plugins {
        create("ktxmlconverter") {
            displayName = "ktxmlconverter"
            id = "$group"
            description = "Gradle plugin to convert Android XML resources into Kotlin objects using Jetpack Compose. Handles dimensions, colors, and styles."
            implementationClass = "io.github.rf19l.ktxml.KtXMLConverter"
            tags.set(listOf("Android", "Kotlin", "JetpackCompose", "XML", "Resources", "Conversion", "GradlePlugin"))
        }
    }
}

tasks {
    val writeClasspathToFile by creating {
        val outputDir = file("$buildDir/pluginUnderTestMetadata")
        outputs.dir(outputDir)
        doLast {
            val outputFile = file("$outputDir/plugin-classpath.txt")
            val printWriter = PrintWriter(outputFile)
            sourceSets["main"].runtimeClasspath.forEach {
                printWriter.println(it.absolutePath)
            }
            printWriter.close()
        }
    }

    val test by getting(Test::class) {
        dependsOn(writeClasspathToFile)
        useJUnitPlatform()
    }
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
