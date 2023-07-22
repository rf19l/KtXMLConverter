import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream
import java.io.PrintWriter
import java.net.URI

plugins {
    kotlin("jvm") version "1.9.0"
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

group = "com.rf.foster.ktxml"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    testImplementation(gradleTestKit())
}

gradlePlugin {
    plugins {
        create("$group") {
            id = "$group"
            implementationClass = "com.rf.foster.ktxml.KtXMLConverter"
        }
    }
}

publishing {
    repositories {
        /*  maven {
            name = "OSSRH"
            url = URI.create("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                //TODO: Replace these with Maven username and password
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }*/
        maven {

            name = "GitHubPackages"
            url = URI.create("https://github.com/rf19l/KtXMLConverter.git")
            credentials {
                val githubProperties = Properties().apply {
                    FileInputStream("github.properties").use { load(it) } // loading the properties file
                }
                username = githubProperties["ext.user"].toString()
                password = githubProperties["ext.key"].toString()

            }
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
