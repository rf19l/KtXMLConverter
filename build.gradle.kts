import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.PrintWriter
plugins {
    kotlin("jvm") version "1.9.0"
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.2.0"

}

group = "com.rf.foster.ktxml"
version ="1.0.10"

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
            description = "A Gradle plugin to convert XML files to Kotlin data objects"
            implementationClass = "com.rf.foster.ktxml.ktXMLConverter"
            tags.set(listOf("testing", "integrationTesting", "compatibility"))

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
