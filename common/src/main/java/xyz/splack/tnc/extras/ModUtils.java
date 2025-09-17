package xyz.splack.tnc.extras;

import static xyz.splack.tnc.extras.TncExtras.MOD_ID;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/** Utility class providing common helper methods for mod operations. */
public class ModUtils {
    /**
     * Constructs a ResourceLocation using the mod ID and the provided path.
     *
     * @param path The path component (e.g., "textures/entity/example.png")
     * @return A new {@code ResourceLocation} with format "{@code mod_id:path}"
     * @see ResourceLocation#ResourceLocation(String, String)
     */
    public static @NotNull ResourceLocation getResourceLocation(@NotNull String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    /**
     * Checks if a viewer (e.g., a player) is looking at an entity within a specified angle and
     * distance.
     *
     * @param entity The entity being checked (e.g., a Goreye).
     * @param viewer The entity looking at the target (e.g., a player).
     * @param maxAngle The maximum angle (in degrees) between the viewer's look direction and the
     *     entity.
     * @param maxDistance The maximum distance (in blocks) for the check.
     * @return True if the viewer is looking at the entity within the specified angle and distance,
     *     false otherwise.
     */
    public static boolean lookedAt(
            @NotNull Entity entity, @NotNull LivingEntity viewer, double maxAngle, double maxDistance) {
        // Get vector from viewer's eye position to entity's eye position
        Vec3 toEntity =
                entity.getEyePosition().subtract(viewer.getEyePosition()).normalize();
        // Get viewer's look direction
        Vec3 lookDirection = viewer.getLookAngle();
        // Calculate dot product to find angle between vectors
        double dot = toEntity.dot(lookDirection);
        // Convert maxAngle to cosine for comparison
        double cosAngle = Math.cos(Math.toRadians(maxAngle));
        // Check distance between entities
        double distance = viewer.distanceTo(entity);
        // Return true if entity is within the angle and distance
        return dot > cosAngle && distance <= maxDistance;
    }

    /**
     * Checks if the entity is in a night or dark environment.
     *
     * @param entity The entity to check.
     * @return True if the entity is in a night or dark environment, false otherwise.
     */
    public static boolean isNightOrDark(Entity entity) {
        return entity.level().getDayTime() % 24000 > 13000
                || entity.level().getMaxLocalRawBrightness(entity.blockPosition()) < 4;
    }
}
