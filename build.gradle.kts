import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30"
    application
}

group = "com.google.assistant.actions.migration"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("junit", "junit", "4.13.1")
}

application {
    mainClass.set("com.google.assistant.actions.MainKt")
}

tasks {
    listOf("compileKotlin", "compileTestKotlin").forEach {
        getByName<KotlinCompile>(it) {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    getByName<JavaExec>("run") {
        // Convert project properties into a list of arguments for the main function.
        doFirst {
            val actionsPropertyName = "actions"
            val shortcutsPropertyName = "shortcuts"
            if (!project.hasProperty(actionsPropertyName)) {
                throw GradleException("Project property '$actionsPropertyName' not set")
            }
            if (!project.hasProperty(shortcutsPropertyName)) {
                throw GradleException("Project property '$shortcutsPropertyName' not set")
            }

            args = listOf(
                project.property(actionsPropertyName).toString(),
                project.property(shortcutsPropertyName).toString()
            )
        }
    }
}
