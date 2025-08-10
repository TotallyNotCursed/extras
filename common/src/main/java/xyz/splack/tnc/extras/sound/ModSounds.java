package xyz.splack.tnc.extras.sound;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.splack.tnc.extras.ModUtils;
import xyz.splack.tnc.extras.TncExtras;

public class ModSounds {
  private static final DeferredRegister<SoundEvent> SOUND_EVENTS =
      DeferredRegister.create(TncExtras.MOD_ID, Registries.SOUND_EVENT);
  private static final Logger LOGGER = LoggerFactory.getLogger("TNC Extras Sound Registration");

  // ===============================
  // Items
  // ===============================
  public static RegistrySupplier<SoundEvent> PULLSTONE_RING_USE;
  public static RegistrySupplier<SoundEvent> SANCTIFIED_NOVA_USE;

  // ===============================
  // Armor
  // ===============================
  public static RegistrySupplier<SoundEvent> COLDVEIN_ARMOR_EQUIP;

  public static void register() {
    LOGGER.info("Registering sounds");

    // ===============================
    // Items
    // ===============================
    PULLSTONE_RING_USE =
        SOUND_EVENTS.register(
            "item.pullstone_ring.use",
            () ->
                SoundEvent.createVariableRangeEvent(
                    ModUtils.getResourceLocation("item.pullstone_ring.use")));
    SANCTIFIED_NOVA_USE =
        SOUND_EVENTS.register(
            "item.sanctified_nova.use",
            () ->
                SoundEvent.createVariableRangeEvent(
                    ModUtils.getResourceLocation("item.sanctified_nova.use")));

    // ===============================
    // Armor
    // ===============================
    COLDVEIN_ARMOR_EQUIP =
        SOUND_EVENTS.register(
            "item.armor.equip_coldvein",
            () ->
                SoundEvent.createVariableRangeEvent(
                    ModUtils.getResourceLocation("item.armor.equip_coldvein")));

    SOUND_EVENTS.register();
    LOGGER.info("Sounds registered successfully");
  }
}
