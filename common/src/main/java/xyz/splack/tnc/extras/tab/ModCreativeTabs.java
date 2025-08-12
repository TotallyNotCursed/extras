package xyz.splack.tnc.extras.tab;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.splack.tnc.extras.TncExtras;
import xyz.splack.tnc.extras.item.ModItems;

public class ModCreativeTabs {
  private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
      DeferredRegister.create(TncExtras.MOD_ID, Registries.CREATIVE_MODE_TAB);
  private static final Logger LOGGER =
      LoggerFactory.getLogger("TNC Extras Creative Tab Registration");

  public static RegistrySupplier<CreativeModeTab> TNC_EXTRAS;
  public static RegistrySupplier<CreativeModeTab> TNC_EXTRAS_WIP;

  public static void register() {
    LOGGER.info("Registering creative tabs");

    TNC_EXTRAS =
        CREATIVE_TABS.register(
            "tnc_extras",
            () ->
                CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .title(Component.translatable("itemGroup.tnc_extras"))
                    .icon(() -> ModItems.COLDVEIN.get().getDefaultInstance())
                    .build());
    TNC_EXTRAS_WIP =
        CREATIVE_TABS.register(
            "tnc_extras_wip",
            () ->
                CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
                    .title(Component.translatable("itemGroup.tnc_extras_wip"))
                    .icon(() -> ModItems.GOREYE_SPAWN_EGG.get().getDefaultInstance())
                    .build());

    CREATIVE_TABS.register();
    LOGGER.info("Creative tabs registered successfully");
  }
}
