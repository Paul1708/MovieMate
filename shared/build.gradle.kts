import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

val generatedSecretsDir = layout.buildDirectory.dir("generated/secrets/commonMain")

abstract class GenerateSecretsTask : DefaultTask() {
    @get:InputFile
    @get:Optional
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val localPropertiesFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val propertiesFile = localPropertiesFile.asFile.orNull
        val localProperties = Properties().apply {
            if (propertiesFile?.exists() == true) {
                propertiesFile.inputStream().use(::load)
            }
        }

        val omdbApiKey = System.getenv("OMDB_API_KEY")
            ?.trim()
            ?.takeIf(String::isNotBlank)
            ?: localProperties.getProperty("OMDB_API_KEY")?.trim().orEmpty()

        if (omdbApiKey.isBlank()) {
            throw GradleException(
                "OMDB_API_KEY is missing. Add it to local.properties or set the " +
                    "OMDB_API_KEY environment variable."
            )
        }

        val packageDirectory = outputDir.asFile.get().resolve(
            "de/fh/erfurt/moviemate/config"
        )
        packageDirectory.mkdirs()

        val escapedApiKey = omdbApiKey
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")

        packageDirectory.resolve("Secrets.kt").writeText(
            """
            package de.fh.erfurt.moviemate.config

            internal object Secrets {
                const val OMDB_API_KEY: String = "$escapedApiKey"
            }
            """.trimIndent()
        )
    }
}

val generateSecrets by tasks.registering(GenerateSecretsTask::class) {
    val localPropertiesCandidates = listOf(
        rootProject.layout.projectDirectory.file("local.properties"),
        rootProject.layout.projectDirectory.file("iosApp/local.properties")
    )
    localPropertiesCandidates.firstOrNull { it.asFile.exists() }?.let {
        localPropertiesFile.set(it)
    }
    outputDir.set(generatedSecretsDir)
    outputs.upToDateWhen { false }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm()

    sourceSets {
        commonMain {
            kotlin.srcDir(generatedSecretsDir)

            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutines.extensions)
            }
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.sqlite.driver)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlin.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.coroutines.test)
        }

        androidUnitTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlin.testJunit)
            implementation(libs.junit)
        }

        jvmTest.dependencies {
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.kotlin.testJunit)
        }
    }
}

android {
    namespace = "de.fh.erfurt.moviemate.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

sqldelight {
    databases {
        create("MovieMateDatabase") {
            packageName.set("de.fh.erfurt.moviemate.movies.data.local.db")
        }
    }
}

tasks.matching {
    it.name.contains("compile", ignoreCase = true) &&
            it.name.contains("Kotlin", ignoreCase = true)
}.configureEach {
    dependsOn(generateSecrets)
}
