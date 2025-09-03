architectury {
    platformSetupLoomIde()
    fabric()
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")
    modImplementation("dev.architectury:architectury-fabric:${rootProject.property("architectury_api_version")}")

    "shadowBundle"(project(path = ":common", configuration = "transformProductionFabric"))
}

tasks.named<ProcessResources>("processResources") {
    // Workaround for Architectury Loom issue #288:
    // Include :common resources manually for Fabric runClient,
    // which otherwise misses textures/lang files.
    // See: https://github.com/architectury/architectury-loom/issues/288
    from(project(":common").sourceSets["main"].resources.srcDirs)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version, "description" to project.description)
    }
}
