package xyz.splack.tnc.extras.cape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.splack.tnc.extras.ModUtils;

public class CapeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("TNC Extras Cape Manager");

    private static final Map<UUID, List<String>> PLAYER_CAPES = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> PLAYER_CAPE_SELECTION = new ConcurrentHashMap<>();

    private static final Map<String, ResourceLocation> CAPE_ASSETS = Map.of(
            "red_creeper",
            ModUtils.getResourceLocation("textures/cape/red_creeper.png"),
            "endermans_eclipse",
            ModUtils.getResourceLocation("textures/cape/endermans_eclipse.png"),
            "halloween_2025",
            ModUtils.getResourceLocation("textures/cape/halloween_2025.png"));

    private static final Map<String, String> CAPE_DISPLAY_NAMES = Map.of(
            "red_creeper",
            "Red Creeper",
            "endermans_eclipse",
            "Enderman's Eclipse",
            "halloween_2025",
            "Halloween 2025");

    private static final String CAPE_JSON_URL_FMT =
            "https://raw.githubusercontent.com/TotallyNotCursed/capes/main/src/%s.json";
    private static final String STORAGE_FILE = "tnc_capes.json";
    private static final int HTTP_TIMEOUT_MS = 3000;

    /**
     * Loads cape configuration for a player from a remote JSON endpoint.
     *
     * @param uuid The player's UUID
     */
    public static void loadCapesFor(UUID uuid) {
        String url = String.format(CAPE_JSON_URL_FMT, uuid.toString());
        Optional<String> response = ModUtils.httpGet(url, HTTP_TIMEOUT_MS);

        if (response.isEmpty()) {
            LOGGER.info("No cape JSON found for player {}", uuid);
            clearPlayerCapes(uuid);
            return;
        }

        try {
            JsonObject obj = ModUtils.getGson().fromJson(response.get(), JsonObject.class);
            List<String> capes = parseCapeList(obj, uuid);

            if (!capes.isEmpty()) {
                boolean hadPreviousSelection = PLAYER_CAPE_SELECTION.containsKey(uuid);
                Integer previousSelection = PLAYER_CAPE_SELECTION.get(uuid);

                PLAYER_CAPES.put(uuid, capes);

                if (!hadPreviousSelection) {
                    int defaultIndex = obj.has("default") ? obj.get("default").getAsInt() : -1;
                    setSelectedCape(uuid, defaultIndex);
                } else if (previousSelection != null && previousSelection >= capes.size()) {
                    LOGGER.warn(
                            "Saved cape selection {} is out of bounds for player {} (has {} capes), resetting to default",
                            previousSelection,
                            uuid,
                            capes.size());
                    int defaultIndex = obj.has("default") ? obj.get("default").getAsInt() : -1;
                    setSelectedCape(uuid, defaultIndex);
                }

                LOGGER.info("Loaded {} cape(s) for player {}", capes.size(), uuid);
            } else {
                clearPlayerCapes(uuid);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to parse cape JSON for player {}: {}", uuid, e.getMessage());
            clearPlayerCapes(uuid);
        }
    }

    /**
     * Parses the cape list from a JSON object, validating against available assets.
     *
     * @param obj  The JSON object containing cape configuration
     * @param uuid The player's UUID (for logging)
     * @return A list of valid cape names
     */
    private static List<String> parseCapeList(JsonObject obj, UUID uuid) {
        List<String> capes = new ArrayList<>();
        if (!obj.has("capes")) {
            return capes;
        }

        JsonArray arr = obj.getAsJsonArray("capes");
        for (JsonElement element : arr) {
            String capeName = element.getAsString();
            if (CAPE_ASSETS.containsKey(capeName)) {
                capes.add(capeName);
            } else {
                LOGGER.warn("Cape '{}' not found in assets, skipping for player {}", capeName, uuid);
            }
        }
        return capes;
    }

    /**
     * Removes all cape data for a player.
     *
     * @param uuid The player's UUID
     */
    private static void clearPlayerCapes(UUID uuid) {
        PLAYER_CAPES.remove(uuid);
        PLAYER_CAPE_SELECTION.remove(uuid);
    }

    /**
     * Gets the list of cape names available to a player.
     *
     * @param uuid The player's UUID
     * @return An unmodifiable list of cape names
     */
    public static List<String> getCapeNames(UUID uuid) {
        return PLAYER_CAPES.getOrDefault(uuid, Collections.emptyList());
    }

    /**
     * Gets the display name for a cape ID.
     *
     * @param capeId The internal cape ID
     * @return The user-friendly display name, or the cape ID if not found
     */
    public static String getCapeDisplayName(String capeId) {
        return CAPE_DISPLAY_NAMES.getOrDefault(capeId, capeId);
    }

    /**
     * Gets the internal cape ID from a display name or ID.
     * Supports both display names (case-insensitive) and internal IDs.
     *
     * @param nameOrId The display name or internal ID
     * @return The internal cape ID, or null if not found
     */
    @Nullable
    public static String getCapeIdFromName(String nameOrId) {
        // First check if it's a direct match with an internal ID
        if (CAPE_ASSETS.containsKey(nameOrId)) {
            return nameOrId;
        }

        // Then check display names (case-insensitive)
        for (var entry : CAPE_DISPLAY_NAMES.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(nameOrId)) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Sets the selected cape for a player.
     *
     * @param uuid  The player's UUID
     * @param index The index of the cape to select, or -1 for vanilla cape
     */
    public static void setSelectedCape(UUID uuid, int index) {
        if (index >= -1) {
            List<String> capes = getCapeNames(uuid);
            if (index == -1 || index < capes.size()) {
                PLAYER_CAPE_SELECTION.put(uuid, index);
            } else {
                LOGGER.warn("Invalid cape index {} for player {} (has {} capes)", index, uuid, capes.size());
            }
        }
    }

    /**
     * Gets the index of the selected cape for a player.
     *
     * @param uuid The player's UUID
     * @return The selected cape index, or -1 for vanilla cape
     */
    public static int getSelectedCape(UUID uuid) {
        return PLAYER_CAPE_SELECTION.getOrDefault(uuid, -1);
    }

    /**
     * Gets the ResourceLocation for a player's selected cape.
     *
     * @param uuid The player's UUID
     * @return The cape's ResourceLocation, or null to use vanilla cape
     */
    @Nullable
    public static ResourceLocation getSelectedCapeResource(UUID uuid) {
        int index = getSelectedCape(uuid);
        if (index < 0) {
            return null;
        }

        List<String> names = getCapeNames(uuid);
        if (index < names.size()) {
            return CAPE_ASSETS.get(names.get(index));
        }
        return null;
    }

    /**
     * Save the current PLAYER_CAPES and PLAYER_CAPE_SELECTION maps to disk in the given directory.
     * Call this on world/server save.
     *
     * @param dir The directory to save the file in
     */
    public static void saveToDisk(File dir) {
        try {
            JsonObject root = serializeToJson();
            ModUtils.saveJsonToFile(dir, STORAGE_FILE, root);
            LOGGER.info("Saved cape data to {}", STORAGE_FILE);
        } catch (Exception e) {
            LOGGER.warn("Failed to save cape data: {}", e.getMessage());
        }
    }

    /**
     * Trigger an immediate save of cape data for a player's world.
     * Should be called when a player changes their cape selection.
     *
     * @param server The MinecraftServer instance
     */
    public static void saveImmediately(net.minecraft.server.MinecraftServer server) {
        if (server != null) {
            File dir = server.getWorldPath(LevelResource.ROOT).toFile();
            saveToDisk(dir);
        }
    }

    /**
     * Serializes the current cape data to a JSON object.
     *
     * @return A JsonObject containing all cape data
     */
    @NotNull
    private static JsonObject serializeToJson() {
        JsonObject root = new JsonObject();

        JsonObject selections = new JsonObject();
        for (var entry : PLAYER_CAPE_SELECTION.entrySet()) {
            selections.addProperty(entry.getKey().toString(), entry.getValue());
        }
        root.add("selections", selections);

        JsonObject capes = new JsonObject();
        for (var entry : PLAYER_CAPES.entrySet()) {
            JsonArray arr = new JsonArray();
            for (String name : entry.getValue()) {
                arr.add(name);
            }
            capes.add(entry.getKey().toString(), arr);
        }
        root.add("capes", capes);
        return root;
    }

    /**
     * Load PLAYER_CAPES and PLAYER_CAPE_SELECTION maps from disk in the given directory.
     * Call this on world/server load.
     *
     * @param dir The directory to load the file from
     */
    public static void loadFromDisk(File dir) {
        Optional<JsonObject> rootOpt = ModUtils.loadJsonFromFile(dir, STORAGE_FILE);
        if (rootOpt.isEmpty()) {
            LOGGER.info("No cape data found, starting fresh");
            return;
        }

        try {
            JsonObject root = rootOpt.get();
            deserializeFromJson(root);
            LOGGER.info("Loaded cape data from {}", STORAGE_FILE);
        } catch (Exception e) {
            LOGGER.warn("Failed to load cape data: {}", e.getMessage());
        }
    }

    /**
     * Deserializes cape data from a JSON object.
     *
     * @param root The JSON object to deserialize from
     */
    private static void deserializeFromJson(JsonObject root) {
        PLAYER_CAPE_SELECTION.clear();
        if (root.has("selections")) {
            JsonObject selections = root.getAsJsonObject("selections");
            for (String key : selections.keySet()) {
                try {
                    UUID uuid = UUID.fromString(key);
                    int index = selections.get(key).getAsInt();
                    PLAYER_CAPE_SELECTION.put(uuid, index);
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Invalid UUID in cape selections: {}", key);
                }
            }
        }

        PLAYER_CAPES.clear();
        if (root.has("capes")) {
            JsonObject capes = root.getAsJsonObject("capes");
            for (String key : capes.keySet()) {
                try {
                    UUID uuid = UUID.fromString(key);
                    JsonArray arr = capes.getAsJsonArray(key);
                    List<String> list = new ArrayList<>();
                    for (JsonElement el : arr) {
                        list.add(el.getAsString());
                    }
                    PLAYER_CAPES.put(uuid, list);
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Invalid UUID in cape list: {}", key);
                }
            }
        }
    }
}
