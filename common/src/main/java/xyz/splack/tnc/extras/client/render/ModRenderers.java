package xyz.splack.tnc.extras.client.render;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.splack.tnc.extras.entity.ModEntities;

@Environment(EnvType.CLIENT)
public class ModRenderers {
  private static final Logger LOGGER = LoggerFactory.getLogger("TNC Extras Renderer Registration");

  public static void register() {
    LOGGER.info("Registering entity renderers");

    EntityRendererRegistry.register(ModEntities.GOREYE, GoreyeRenderer::new);

    LOGGER.info("Entity renderers registered successfully");
  }
}
