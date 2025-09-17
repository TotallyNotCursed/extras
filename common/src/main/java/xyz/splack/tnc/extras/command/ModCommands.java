package xyz.splack.tnc.extras.command;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModCommands {
    private static final Logger LOGGER = LoggerFactory.getLogger("TNC Extras Command Registration");

    public static void register() {
        LOGGER.info("Registering commands");

        // Register the TncCommand
        CommandRegistrationEvent.EVENT.register(
                (dispatcher, registryAccess, environment) -> TncCommand.register(dispatcher));

        LOGGER.info("Commands registered successfully");
    }
}
