pluginManagement {
  repositories {
    mavenCentral()
    maven { url = uri("https://maven.fabricmc.net/") }
    maven { url = uri("https://maven.architectury.dev/") }
    maven { url = uri("https://files.minecraftforge.net/maven/") }
    gradlePluginPortal()
  }
}

rootProject.name = "tnc_extras"

include("common")
include("fabric")
include("forge")
