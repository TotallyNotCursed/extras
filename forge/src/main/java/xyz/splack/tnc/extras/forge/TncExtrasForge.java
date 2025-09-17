package xyz.splack.tnc.extras.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.splack.tnc.extras.TncExtras;

@Mod(TncExtras.MOD_ID)
public final class TncExtrasForge {
    public TncExtrasForge(FMLJavaModLoadingContext context) {
        var bus = context.getModEventBus();
        EventBuses.registerModEventBus(TncExtras.MOD_ID, bus);

        TncExtras.init();
    }
}
