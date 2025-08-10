package xyz.splack.tnc.extras.worldgen;

import dev.architectury.registry.level.biome.BiomeModifications;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.splack.tnc.extras.ModUtils;

@SuppressWarnings("UnstableApiUsage")
public class ModOreFeatures {
  private static final ResourceKey<PlacedFeature> ORE_COLDVEIN_SMALL_KEY =
      ResourceKey.create(
          Registries.PLACED_FEATURE, ModUtils.getResourceLocation("ore_coldvein_small"));
  private static final ResourceKey<PlacedFeature> ORE_COLDVEIN_LARGE_KEY =
      ResourceKey.create(
          Registries.PLACED_FEATURE, ModUtils.getResourceLocation("ore_coldvein_large"));
  private static final Logger LOGGER =
      LoggerFactory.getLogger("TNC Extras Ore Feature Registration");

  public static void register() {
    LOGGER.info("Registering ore features");

    BiomeModifications.addProperties(
        (ctx) -> ctx.hasTag(BiomeTags.IS_OVERWORLD),
        (ctx, mutable) -> {
          mutable
              .getGenerationProperties()
              .addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_COLDVEIN_SMALL_KEY);
          mutable
              .getGenerationProperties()
              .addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_COLDVEIN_LARGE_KEY);
        });

    LOGGER.info("Ore features registered successfully");
  }
}
