package xyz.splack.tnc.extras.cape;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import java.io.File;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.splack.tnc.extras.ModUtils;

public class CapeEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("TNC Extras Cape Event Handler");

    public static void register() {
        LOGGER.info("Registering cape events");

        PlayerEvent.PLAYER_JOIN.register(CapeEventHandler::onPlayerJoin);
        LifecycleEvent.SERVER_STARTING.register(CapeEventHandler::onServerStart);
        LifecycleEvent.SERVER_STOPPING.register(CapeEventHandler::onServerStop);

        LOGGER.info("Cape events registered successfully");
    }

    private static void onPlayerJoin(ServerPlayer player) {
        ModUtils.runAsync("CapeLoad-" + player.getGameProfile().getName(), () -> {
            try {
                LOGGER.info("Loading capes for player: {}", player.getUUID());
                CapeManager.loadCapesFor(player.getUUID());
            } catch (Exception e) {
                LOGGER.warn("Failed to load capes for player {}: {}", player.getUUID(), e.getMessage());
            }
        });
    }

    private static void onServerStart(MinecraftServer server) {
        File dir = server.getWorldPath(LevelResource.ROOT).toFile();
        CapeManager.loadFromDisk(dir);
    }

    private static void onServerStop(MinecraftServer server) {
        File dir = server.getWorldPath(LevelResource.ROOT).toFile();
        CapeManager.saveToDisk(dir);
    }
}
