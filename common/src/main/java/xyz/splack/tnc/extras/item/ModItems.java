package xyz.splack.tnc.extras.item;

import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.splack.tnc.extras.TncExtras;
import xyz.splack.tnc.extras.block.ModBlocks;
import xyz.splack.tnc.extras.entity.ModEntities;
import xyz.splack.tnc.extras.tab.ModCreativeTabs;

@SuppressWarnings("UnstableApiUsage")
public class ModItems {
  private static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(TncExtras.MOD_ID, Registries.ITEM);
  private static final Logger LOGGER = LoggerFactory.getLogger("TNC Extras Item Registration");

  // ===============================
  // Armor
  // ===============================
  public static RegistrySupplier<Item> COLDVEIN_BOOTS;
  public static RegistrySupplier<Item> COLDVEIN_CHESTPLATE;
  public static RegistrySupplier<Item> COLDVEIN_HELMET;
  public static RegistrySupplier<Item> COLDVEIN_LEGGINGS;

  // ===============================
  // Block items
  // ===============================
  // Ores
  public static RegistrySupplier<BlockItem> COLDVEIN_ORE;
  public static RegistrySupplier<BlockItem> DEEPSLATE_COLDVEIN_ORE;

  // ===============================
  // Miscellaneous
  // ===============================
  public static RegistrySupplier<Item> COLDVEIN;
  public static RegistrySupplier<Item> PULLSTONE_RING;
  public static RegistrySupplier<Item> SANCTIFIED_NOVA;

  // ===============================
  // Spawn eggs
  // ===============================
  public static RegistrySupplier<Item> GOREYE_SPAWN_EGG;

  // ===============================
  // Tools
  // ===============================
  public static RegistrySupplier<Item> COLDVEIN_AXE;
  public static RegistrySupplier<Item> COLDVEIN_HOE;
  public static RegistrySupplier<Item> COLDVEIN_PICKAXE;
  public static RegistrySupplier<Item> COLDVEIN_SHOVEL;
  public static RegistrySupplier<Item> COLDVEIN_SWORD;

  public static void register() {
    LOGGER.info("Registering items");

    // ===============================
    // Armor
    // ===============================
    COLDVEIN_BOOTS =
        ITEMS.register(
            "coldvein_boots",
            () ->
                new ArmorItem(
                    ModArmorMaterials.COLDVEIN,
                    ArmorItem.Type.BOOTS,
                    new Item.Properties().arch$tab(ModCreativeTabs.TNC_EXTRAS)));
    COLDVEIN_CHESTPLATE =
        ITEMS.register(
            "coldvein_chestplate",
            () ->
                new ArmorItem(
                    ModArmorMaterials.COLDVEIN,
                    ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().arch$tab(ModCreativeTabs.TNC_EXTRAS)));
    COLDVEIN_HELMET =
        ITEMS.register(
            "coldvein_helmet",
            () ->
                new ArmorItem(
                    ModArmorMaterials.COLDVEIN,
                    ArmorItem.Type.HELMET,
                    new Item.Properties().arch$tab(ModCreativeTabs.TNC_EXTRAS)));
    COLDVEIN_LEGGINGS =
        ITEMS.register(
            "coldvein_leggings",
            () ->
                new ArmorItem(
                    ModArmorMaterials.COLDVEIN,
                    ArmorItem.Type.LEGGINGS,
                    new Item.Properties().arch$tab(ModCreativeTabs.TNC_EXTRAS)));

    // ===============================
    // Block items
    // ===============================
    // Ores
    COLDVEIN_ORE =
        ITEMS.register(
            "coldvein_ore",
            () ->
                new BlockItem(
                    ModBlocks.COLDVEIN_ORE.get(),
                    new Item.Properties().arch$tab(ModCreativeTabs.TNC_EXTRAS)));
    DEEPSLATE_COLDVEIN_ORE =
        ITEMS.register(
            "deepslate_coldvein_ore",
            () ->
                new BlockItem(
                    ModBlocks.DEEPSLATE_COLDVEIN_ORE.get(),
                    new Item.Properties().arch$tab(ModCreativeTabs.TNC_EXTRAS)));

    // ===============================
    // Miscellaneous
    // ===============================
    COLDVEIN =
        ITEMS.register(
            "coldvein", () -> new Item(new Item.Properties().arch$tab(ModCreativeTabs.TNC_EXTRAS)));
    PULLSTONE_RING =
        ITEMS.register(
            "pullstone_ring",
            () ->
                new PullstoneRingItem(new Item.Properties().arch$tab(ModCreativeTabs.TNC_EXTRAS)));
    SANCTIFIED_NOVA =
        ITEMS.register(
            "sanctified_nova",
            () ->
                new SanctifiedNovaItem(new Item.Properties().arch$tab(ModCreativeTabs.TNC_EXTRAS)));

    // ===============================
    // Spawn eggs
    // ===============================
    GOREYE_SPAWN_EGG =
        ITEMS.register(
            "goreye_spawn_egg",
            () ->
                new ArchitecturySpawnEggItem(
                    ModEntities.GOREYE,
                    0x000000,
                    0xFFFFFF,
                    new Item.Properties().arch$tab(ModCreativeTabs.TNC_EXTRAS_WIP)));

    // ===============================
    // Tools
    // ===============================
    COLDVEIN_AXE =
        ITEMS.register(
            "coldvein_axe",
            () ->
                new AxeItem(
                    ModToolMaterials.COLDVEIN,
                    5,
                    -3.0f,
                    new Item.Properties().arch$tab(ModCreativeTabs.TNC_EXTRAS)));
    COLDVEIN_HOE =
        ITEMS.register(
            "coldvein_hoe",
            () ->
                new HoeItem(
                    ModToolMaterials.COLDVEIN,
                    -1,
                    -1.0f,
                    new Item.Properties().arch$tab(ModCreativeTabs.TNC_EXTRAS)));
    COLDVEIN_PICKAXE =
        ITEMS.register(
            "coldvein_pickaxe",
            () ->
                new PickaxeItem(
                    ModToolMaterials.COLDVEIN,
                    1,
                    -2.8f,
                    new Item.Properties().arch$tab(ModCreativeTabs.TNC_EXTRAS)));
    COLDVEIN_SHOVEL =
        ITEMS.register(
            "coldvein_shovel",
            () ->
                new ShovelItem(
                    ModToolMaterials.COLDVEIN,
                    1.5f,
                    -3.0f,
                    new Item.Properties().arch$tab(ModCreativeTabs.TNC_EXTRAS)));
    COLDVEIN_SWORD =
        ITEMS.register(
            "coldvein_sword",
            () ->
                new SwordItem(
                    ModToolMaterials.COLDVEIN,
                    3,
                    -2.4f,
                    new Item.Properties().arch$tab(ModCreativeTabs.TNC_EXTRAS)));

    ITEMS.register();
    LOGGER.info("Items registered successfully");
  }
}
