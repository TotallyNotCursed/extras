package xyz.splack.tnc.extras.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.splack.tnc.extras.TncExtras;

public class ModBlocks {
    private static final Logger LOGGER = LoggerFactory.getLogger("TNC Extras Block Registration");
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(TncExtras.MOD_ID, Registries.BLOCK);

    // ===============================
    // Ores
    // ===============================
    public static RegistrySupplier<ColdveinOreBlock> COLDVEIN_ORE;
    public static RegistrySupplier<DeepslateColdveinOreBlock> DEEPSLATE_COLDVEIN_ORE;

    public static void register() {
        LOGGER.info("Registering blocks");

        // ===============================
        // Ores
        // ===============================
        COLDVEIN_ORE = BLOCKS.register("coldvein_ore", () -> new ColdveinOreBlock(BlockBehaviour.Properties.of()));
        DEEPSLATE_COLDVEIN_ORE = BLOCKS.register(
                "deepslate_coldvein_ore", () -> new DeepslateColdveinOreBlock(BlockBehaviour.Properties.of()));

        BLOCKS.register();
        LOGGER.info("Blocks registered successfully");
    }
}
