import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    kotlin("jvm") version "1.9.0"
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

group = "com.rf.foster.ktxml"
version = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))


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
            implementationClass = "com.rf.foster.ktxml.ktXMLConverter"
            displayName = "ktxmlConverter"
        }
    }
}

// build.gradle
publishing {
    publications {
        create<MavenPublication>("ktXmlConverter") {
            groupId = "${project.group}"
            artifactId = "ktxmlconverter"
            version = "${project.version}"
            from(components["kotlin"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/rf19l/KtXMLConverter")
            credentials {
                username =
                    System.getenv("GITHUB_ACTOR") ?: ""
                password =
                    System.getenv("GITHUB_TOKEN") ?: ""
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
