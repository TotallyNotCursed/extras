package xyz.splack.tnc.extras.check;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.splack.tnc.extras.ModConstants;

public final class ModpackCheck {
  private static final Logger LOGGER = LoggerFactory.getLogger("TNC Extras Modpack Check");

  public static ValidationResult validateModpack() {
    String expectedVersion = ModConstants.MOD_VERSION;
    Path packVersionFile =
        Minecraft.getInstance().gameDirectory.toPath().resolve(ModConstants.PACK_VERSION_FILE);

    if (!Files.exists(packVersionFile)) {
      String message = "Missing modpack version file: " + ModConstants.PACK_VERSION_FILE;
      LOGGER.error(message);
      return ValidationResult.invalid(message, null, expectedVersion);
    }

    try {
      String installedVersion = readVersionFile(packVersionFile);
      if (installedVersion == null) {
        String message = "Empty or unreadable version file: " + ModConstants.PACK_VERSION_FILE;
        LOGGER.error(message);
        return ValidationResult.invalid(message, null, expectedVersion);
      }

      if (!installedVersion.equals(expectedVersion)) {
        String message =
            String.format(
                "Version mismatch: expected %s, found %s", expectedVersion, installedVersion);
        LOGGER.error(message);
        return ValidationResult.invalid(message, installedVersion, expectedVersion);
      }

      LOGGER.info("Modpack version validated: {}", installedVersion);
      return ValidationResult.valid(installedVersion, expectedVersion);

    } catch (IOException e) {
      String message = "Failed to validate modpack: " + e.getMessage();
      LOGGER.error(message, e);
      return ValidationResult.invalid(message, null, expectedVersion);
    }
  }

  private static String readVersionFile(Path packVersionFile) throws IOException {
    String content = Files.readString(packVersionFile).trim();
    return content.isEmpty() ? null : content;
  }

  public record ValidationResult(
      boolean isValid, String message, String installedVersion, String expectedVersion) {
    public static ValidationResult valid(String installedVersion, String expectedVersion) {
      return new ValidationResult(
          true, "Modpack version is valid", installedVersion, expectedVersion);
    }

    public static ValidationResult invalid(
        String message, String installedVersion, String expectedVersion) {
      return new ValidationResult(false, message, installedVersion, expectedVersion);
    }
  }
}
