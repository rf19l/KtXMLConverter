import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
plugins {
    kotlin("jvm") version "1.9.0"
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.2.0"

}

group = "com.rf.foster.ktxml"
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
    website.set("https://github.com/rf19l/KtXMLConverter")
    vcsUrl.set("https://github.com/rf19l/KtXMLConverter.git")
    plugins {
        create("ktxmlconverter") {
            displayName = "ktxmlconverter"
            id = "${project.group}"
            description = "A Gradle plugin to convert XML files to Kotlin data objects"
            implementationClass = "com.rf.foster.ktxml.ktXMLConverter"
            tags.set(listOf("testing", "integrationTesting", "compatibility"))

        }
    }
}

/*
gradlePlugin {
    plugins {
        create("ktxmlconverter") {
            id = "io.github.rf19l"
            implementationClass = "com.rf.foster.ktxml.ktXMLConverter"
        }
    }
}
*/

publishing {
    publications {
        create<MavenPublication>("GitHubPackages") {
            groupId = "${project.group}"
            artifactId = "ktxmlconverter"
            version = version
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/rf19l/KtXMLConverter")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: ""
                password = System.getenv("GITHUB_TOKEN") ?: ""
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
