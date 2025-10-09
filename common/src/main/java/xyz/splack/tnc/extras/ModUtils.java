package xyz.splack.tnc.extras;

import static xyz.splack.tnc.extras.TncExtras.MOD_ID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/** Utility class providing common helper methods for mod operations. */
public class ModUtils {
    private static final Gson GSON = new Gson();

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
     * Checks if a viewer (e.g., a player) is looking at an entity within a specified angle and distance.
     *
     * @param entity The entity being checked (e.g., a Goreye)
     * @param viewer The entity looking at the target (e.g., a player)
     * @param maxAngle The maximum angle (in degrees) between the viewer's look direction and the entity
     * @param maxDistance The maximum distance (in blocks) for the check
     * @return True if the viewer is looking at the entity within the specified angle and distance, false otherwise
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
     * @param entity The entity to check
     * @return True if the entity is in a night or dark environment, false otherwise
     */
    public static boolean isNightOrDark(Entity entity) {
        return entity.level().getDayTime() % 24000 > 13000
                || entity.level().getMaxLocalRawBrightness(entity.blockPosition()) < 4;
    }

    /**
     * Performs an HTTP GET request and returns the response body as a string.
     *
     * @param urlString The URL to request
     * @param timeoutMs Connection timeout in milliseconds
     * @return An Optional containing the response body if successful, empty otherwise
     */
    public static Optional<String> httpGet(@NotNull String urlString, int timeoutMs) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(timeoutMs);
                conn.setReadTimeout(timeoutMs);

                if (conn.getResponseCode() != 200) {
                    return Optional.empty();
                }

                try (BufferedReader in =
                        new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    return Optional.of(response.toString());
                }
            } finally {
                conn.disconnect();
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Saves a JsonObject to a file in the specified directory.
     *
     * @param dir        The directory to save the file in
     * @param fileName   The name of the file
     * @param jsonObject The JsonObject to save
     * @throws IOException If an I/O error occurs
     */
    public static void saveJsonToFile(@NotNull File dir, @NotNull String fileName, @NotNull JsonObject jsonObject)
            throws IOException {
        File file = new File(dir, fileName);
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            GSON.toJson(jsonObject, writer);
        }
    }

    /**
     * Loads a JsonObject from a file in the specified directory.
     *
     * @param dir      The directory to load the file from
     * @param fileName The name of the file
     * @return An Optional containing the JsonObject if successful, empty otherwise
     */
    public static Optional<JsonObject> loadJsonFromFile(@NotNull File dir, @NotNull String fileName) {
        File file = new File(dir, fileName);
        if (!file.exists()) {
            return Optional.empty();
        }
        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            return Optional.ofNullable(GSON.fromJson(reader, JsonObject.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a shared Gson instance for JSON operations.
     *
     * @return The Gson instance
     */
    public static Gson getGson() {
        return GSON;
    }

    /**
     * Executes a task asynchronously in a named daemon thread.
     * This is safer than using raw Thread() for background operations.
     *
     * @param taskName The name for the thread (for debugging)
     * @param task     The task to execute
     */
    public static void runAsync(@NotNull String taskName, @NotNull Runnable task) {
        Thread thread = new Thread(task, taskName);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Gets the game directory in a platform-agnostic way.
     * Works on both client and dedicated server.
     *
     * @return The game directory path (defaults to current directory on servers)
     */
    @NotNull
    public static Path getGameDirectory() {
        try {
            // Try client-side first
            Class<?> minecraftClass = Class.forName("net.minecraft.client.Minecraft");
            Object instance = minecraftClass.getMethod("getInstance").invoke(null);
            // Field name is 'gameDirectory', not a method
            Object gameDir = minecraftClass.getField("gameDirectory").get(instance);
            if (gameDir instanceof File) {
                return ((File) gameDir).toPath();
            }
        } catch (Exception e) {
            // Not on client or client not available, try server
        }

        // Fall back to current working directory (works for servers)
        return Path.of(".");
    }
}
