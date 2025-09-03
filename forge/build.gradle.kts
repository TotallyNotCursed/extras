configure<net.fabricmc.loom.api.LoomGradleExtensionAPI> {
    forge {
        mixinConfig("tnc_extras.mixins.json")
    }
}

architectury {
    platformSetupLoomIde()
    forge()
}

dependencies {
    "forge"("net.minecraftforge:forge:${rootProject.property("forge_version")}")
    modImplementation("dev.architectury:architectury-forge:${rootProject.property("architectury_api_version")}")

    "shadowBundle"(project(path = ":common", configuration = "transformProductionForge"))
}

tasks.named<ProcessResources>("processResources") {
    filesMatching("META-INF/mods.toml") {
        expand(
            "version" to project.version, "description" to project.description
        )
    }
}
