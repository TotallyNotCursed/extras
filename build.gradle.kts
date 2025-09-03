@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("dev.architectury.loom") version "1.10-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("com.gradleup.shadow") version "8.3.6" apply false
    id("io.github.pacifistmc.forgix") version "2.0.0-SNAPSHOT.5.1"
    id("com.diffplug.spotless") version "7.2.1"
}

architectury {
    minecraft = project.property("minecraft_version").toString()
    compileOnly()
}

forgix {
    autoRun = true
    silence = true
}

allprojects {
    repositories {
        maven {
            name = "ParchmentMC"
            url = uri("https://maven.parchmentmc.org")
        }
    }

    group = rootProject.property("maven_group").toString()
    version = rootProject.property("mod_version").toString()
    description = rootProject.property("mod_description").toString()
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")
    apply(plugin = "com.diffplug.spotless")

    spotless {
        java {
            googleJavaFormat("1.28.0")
            removeUnusedImports()

            // F → f
            replaceRegex(
                "Float suffix to lowercase f", "(\\d+(?:\\.\\d*)?(?:[eE][+-]?\\d+)?)[F]\\b", "$1f"
            )

            // D → d
            replaceRegex(
                "Double suffix to lowercase d", "(\\d+(?:\\.\\d*)?(?:[eE][+-]?\\d+)?)[D]\\b", "$1d"
            )

            // L → l
            replaceRegex(
                "Long suffix to lowercase l", "(\\d+)[L]\\b", "$1l"
            )
        }
    }

    base {
        archivesName.set("${rootProject.property("archives_name")}-${project.name}")
    }

    // Access the loom extension correctly for each subproject
    val loom = project.extensions.getByType(LoomGradleExtensionAPI::class.java)

    dependencies {
        "minecraft"("net.minecraft:minecraft:${rootProject.property("minecraft_version")}")
        // Use the 'loom' variable we defined above
        "mappings"(loom.layered {
            officialMojangMappings()
            parchment(
                "org.parchmentmc.data:parchment-${rootProject.property("minecraft_version")}:${
                    rootProject.property(
                        "parchment_mappings"
                    )
                }@zip"
            )
        })

        "modImplementation"("dev.architectury:architectury:${rootProject.property("architectury_api_version")}")
    }

    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<JavaCompile> {
        options.release.set(17)
    }

    tasks.named<ProcessResources>("processResources") {
        inputs.property("version", project.version)
    }
}

configure(subprojects.filter { it.name in listOf("fabric", "forge") }) {
    apply(plugin = "com.gradleup.shadow")

    val common by configurations.creating {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    configurations["compileClasspath"].extendsFrom(common)
    configurations["runtimeClasspath"].extendsFrom(common)

    @Suppress("unused") val shadowBundle by configurations.creating {
        isCanBeResolved = true
        isCanBeConsumed = false
    }

    dependencies {
        common(project(path = ":common", configuration = "namedElements")) {
            isTransitive = false
        }
    }

    tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        configurations = listOf(project.configurations["shadowBundle"])
        archiveClassifier.set("dev-shadow")
    }

    tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
        inputFile.set(
            tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar")
                .flatMap { it.archiveFile })
    }
}
