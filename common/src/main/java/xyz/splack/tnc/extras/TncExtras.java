package xyz.splack.tnc.extras;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.splack.tnc.extras.block.ModBlocks;
import xyz.splack.tnc.extras.cape.CustomCapeRegistry;
import xyz.splack.tnc.extras.client.render.ModRenderers;
import xyz.splack.tnc.extras.command.ModCommands;
import xyz.splack.tnc.extras.entity.ModEntities;
import xyz.splack.tnc.extras.event.ModEvents;
import xyz.splack.tnc.extras.item.ModItems;
import xyz.splack.tnc.extras.sound.ModSounds;
import xyz.splack.tnc.extras.tab.ModCreativeTabs;
import xyz.splack.tnc.extras.worldgen.ModOreFeatures;

public final class TncExtras {
  public static final String MOD_ID = "tnc_extras";
  private static final Logger LOGGER = LoggerFactory.getLogger("TNC Extras");

  public static void init() {
    // Check if running with modpack
    Path packVersionFile =
        Minecraft.getInstance().gameDirectory.toPath().resolve(ModConstants.PACK_VERSION_FILE);

    if (Files.exists(packVersionFile)) {
      try {
        String modpackVersion = Files.readString(packVersionFile).trim();
        if (modpackVersion.isEmpty()) {
          LOGGER.warn("Running with TNC Modpack (version unknown)");
        } else {
          LOGGER.info("Running with TNC Modpack {}", modpackVersion);

          // Check version compatibility
          if (!modpackVersion.equals(ModConstants.MOD_VERSION)) {
            LOGGER.warn("Version mismatch detected!");
            LOGGER.warn("TNC Extras version: {}", ModConstants.MOD_VERSION);
            LOGGER.warn("TNC Modpack version: {}", modpackVersion);
            LOGGER.warn("Some features may not work correctly with mismatched versions");
          }
        }
      } catch (IOException e) {
        LOGGER.warn("Could not read modpack version: {}", e.getMessage());
      }
    } else {
      LOGGER.warn("Running standalone - install TNC Modpack for the full experience");
    }

    // Continue with initialization
    LOGGER.info("Initializing TNC Extras");
    ModBlocks.register();
    ModEntities.register();
    ModEntities.registerAttributes();
    ModCreativeTabs.register();
    ModItems.register();
    ModOreFeatures.register();
    ModSounds.register();
    ModRenderers.register();
    ModEvents.register();
    ModCommands.register();
    CustomCapeRegistry.register();
    LOGGER.info("TNC Extras initialized successfully");
  }
}
