package xyz.splack.tnc.extras.entity;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.splack.tnc.extras.TncExtras;

public class ModEntities {
  public static final DeferredRegister<EntityType<?>> ENTITIES =
      DeferredRegister.create(TncExtras.MOD_ID, Registries.ENTITY_TYPE);
  private static final Logger LOGGER = LoggerFactory.getLogger("TNC Extras Entity Registration");
  public static RegistrySupplier<EntityType<GoreyeEntity>> GOREYE;

  public static void register() {
    LOGGER.info("Registering entities");

    GOREYE =
        ENTITIES.register(
            "goreye",
            () ->
                EntityType.Builder.of(GoreyeEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .clientTrackingRange(8)
                    .updateInterval(3)
                    .build("goreye"));

    ENTITIES.register();
    LOGGER.info("Entities registered successfully");
  }

  /// Registers entity attributes.
  ///
  /// This must be called separately from entity registration to avoid issues with attribute
  /// assignment timing.
  ///
  /// Platform note: Architectury's DeferredRegister behaves differently per loader:
  ///   - On **Forge**, registration is deferred (safe).
  ///   - On **Fabric**, it is immediate. Accessing unregistered objects (like entities) too early
  ///     will cause NullPointerExceptions (NPEs).
  ///
  /// Ensure this method is called only after entity registration is complete on both platforms.
  public static void registerAttributes() {
    LOGGER.info("Registering entity attributes");

    EntityAttributeRegistry.register(ModEntities.GOREYE, GoreyeEntity::createAttributes);

    LOGGER.info("Entity attributes registered successfully");
  }
}
