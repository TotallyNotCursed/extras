package xyz.splack.tnc.extras.event;

import dev.architectury.event.events.common.EntityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModEvents {
    private static final Logger LOGGER = LoggerFactory.getLogger("TNC Extras Event Registration");

    public static void register() {
        LOGGER.info("Registering events");

        EntityEvent.LIVING_DEATH.register(new UndeadDropOnDeathEvent());
        EntityEvent.LIVING_DEATH.register(new EndermanJumpscareOnAnimalKillEvent());

        LOGGER.info("Events registered successfully");
    }
}
