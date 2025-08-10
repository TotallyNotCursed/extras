package xyz.splack.tnc.extras.cape;

import java.util.HashMap;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.splack.tnc.extras.ModUtils;

public class CustomCapeRegistry {
  private static final HashMap<UUID, ResourceLocation> CUSTOM_CAPES = new HashMap<>();
  private static final Logger LOGGER =
      LoggerFactory.getLogger("TNC Extras Custom Cape Registration");

  public static void register() {
    LOGGER.info("Registering custom capes");

    // HeySiMoon
    CUSTOM_CAPES.put(
        UUID.fromString("aad4e22e-b66d-4d09-a574-d79c643ee28d"),
        ModUtils.getResourceLocation("textures/cape/red_creeper.png"));

    LOGGER.info("{} custom cape(s) registered successfully", CUSTOM_CAPES.size());
  }

  public static boolean hasCape(UUID uuid) {
    return CUSTOM_CAPES.containsKey(uuid);
  }

  public static ResourceLocation getCape(UUID uuid) {
    return CUSTOM_CAPES.get(uuid);
  }
}
