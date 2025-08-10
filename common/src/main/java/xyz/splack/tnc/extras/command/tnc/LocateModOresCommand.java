package xyz.splack.tnc.extras.command.tnc;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import xyz.splack.tnc.extras.block.ModBlocks;

public class LocateModOresCommand {
  public static void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
    builder.then(
        Commands.literal("locateModOres")
            .then(
                Commands.argument("radius", IntegerArgumentType.integer(1, 128))
                    .executes(
                        context -> {
                          final CommandSourceStack source = context.getSource();
                          if (!(source.getEntity() instanceof ServerPlayer player)) {
                            source.sendFailure(
                                Component.translatable("command.tnc_extras.player_only")
                                    .withStyle(ChatFormatting.RED));
                            return 0;
                          }
                          final int radius = IntegerArgumentType.getInteger(context, "radius");
                          source.sendSuccess(
                              () ->
                                  Component.translatable(
                                      "command.tnc_extras.locating_ores", radius),
                              false);

                          CompletableFuture.runAsync(
                              () -> {
                                var level = player.level();
                                var pos = player.blockPosition();
                                int found = 0;
                                List<Component> foundOres = new ArrayList<>();

                                for (int x = pos.getX() - radius; x <= pos.getX() + radius; x++) {
                                  for (int y = level.getMinBuildHeight();
                                      y <= level.getMaxBuildHeight();
                                      y++) {
                                    for (int z = pos.getZ() - radius;
                                        z <= pos.getZ() + radius;
                                        z++) {
                                      var checkPos = new BlockPos(x, y, z);
                                      var blockState = level.getBlockState(checkPos);
                                      var block = blockState.getBlock();

                                      if (block == ModBlocks.COLDVEIN_ORE.get()
                                          || block == ModBlocks.DEEPSLATE_COLDVEIN_ORE.get()) {
                                        if (found < 50) {
                                          foundOres.add(
                                              Component.translatable(
                                                  "command.tnc_extras.found_ore_at",
                                                  block.getName(),
                                                  x,
                                                  y,
                                                  z));
                                        }
                                        found++;
                                      }
                                    }
                                  }
                                }

                                final int finalFound = found;
                                if (finalFound == 0) {
                                  source.sendSuccess(
                                      () ->
                                          Component.translatable(
                                              "command.tnc_extras.no_ores_found", radius),
                                      false);
                                } else {
                                  source.sendSuccess(
                                      () ->
                                          Component.translatable(
                                              "command.tnc_extras.found_ores_summary",
                                              finalFound,
                                              radius),
                                      false);
                                  foundOres.forEach(c -> source.sendSuccess(() -> c, false));
                                  if (finalFound > 50) {
                                    source.sendSuccess(
                                        () ->
                                            Component.translatable(
                                                "command.tnc_extras.found_ores_truncated"),
                                        false);
                                  }
                                }
                              },
                              player.getServer());

                          return 1;
                        })));
  }
}
