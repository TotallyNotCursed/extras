package xyz.splack.tnc.extras;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.splack.tnc.extras.block.ModBlocks;
import xyz.splack.tnc.extras.cape.CustomCapeRegistry;
import xyz.splack.tnc.extras.check.ModpackCheck;
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
    ModpackCheck.ValidationResult result = ModpackCheck.validateModpack();
    if (!result.isValid()) {
      String errorMessage =
          String.format(
              "TNC Extras requires TNC Modpack %s. Issue: %s. Please install the correct modpack version.",
              result.expectedVersion(), result.message());
      LOGGER.error(errorMessage);
      throw new IllegalStateException(errorMessage);
    }

    LOGGER.info("Initializing TNC Extras for modpack {}", result.installedVersion());
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
