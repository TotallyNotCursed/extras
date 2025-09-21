package xyz.splack.tnc.extras.command.tnc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class UuidCommand {
    public static void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.then(Commands.literal("uuid")
                .requires(source -> true) // No permission required
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    if (source.getEntity() instanceof ServerPlayer player) {
                        source.sendSuccess(
                                () -> Component.literal("Your UUID: ")
                                        .withStyle(ChatFormatting.GRAY)
                                        .append(Component.literal(player.getStringUUID())
                                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)),
                                false);
                        return 1;
                    } else {
                        source.sendFailure(Component.translatable("command.tnc_extras.player_only")
                                .withStyle(ChatFormatting.RED));
                        return 0;
                    }
                })
                .then(Commands.argument("target", EntityArgument.player()).executes(context -> {
                    ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "target");
                    context.getSource()
                            .sendSuccess(
                                    () -> Component.literal(
                                                    targetPlayer.getName().getString() + "'s UUID: ")
                                            .withStyle(ChatFormatting.GRAY)
                                            .append(Component.literal(targetPlayer.getStringUUID())
                                                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)),
                                    false);
                    return 1;
                })));
    }
}
