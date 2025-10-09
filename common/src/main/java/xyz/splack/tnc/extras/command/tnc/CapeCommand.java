package xyz.splack.tnc.extras.command.tnc;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import xyz.splack.tnc.extras.ModUtils;
import xyz.splack.tnc.extras.cape.CapeManager;

public class CapeCommand {

    private static final SuggestionProvider<CommandSourceStack> CAPE_SUGGESTIONS = (context, builder) -> {
        ServerPlayer player = getPlayerFromContext(context);
        if (player != null) {
            List<String> capes = CapeManager.getCapeNames(player.getUUID());
            SuggestionsBuilder suggestionsBuilder =
                    builder.createOffset(builder.getInput().lastIndexOf(' ') + 1);

            suggestionsBuilder.suggest("none");

            for (String capeId : capes) {
                String displayName = CapeManager.getCapeDisplayName(capeId);
                suggestionsBuilder.suggest(displayName);
            }

            return suggestionsBuilder.buildFuture();
        }
        return Suggestions.empty();
    };

    public static void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.then(Commands.literal("cape")
                .requires(CommandSourceStack::isPlayer)
                .then(Commands.literal("list").executes(CapeCommand::listCapes))
                .then(Commands.literal("set")
                        .then(Commands.argument("cape_name", StringArgumentType.greedyString())
                                .suggests(CAPE_SUGGESTIONS)
                                .executes(CapeCommand::setCapeByName)))
                .then(Commands.literal("current").executes(CapeCommand::showCurrentCape))
                .then(Commands.literal("reload").executes(CapeCommand::reloadCapes))
                .executes(CapeCommand::showHelp));
    }

    @Nullable
    private static ServerPlayer getPlayerFromContext(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            return player;
        }
        return null;
    }

    private static void sendCapeNotFoundMessage(ServerPlayer player, String capeName) {
        player.sendSystemMessage(Component.translatable("command.tnc_extras.cape_not_found", capeName)
                .withStyle(ChatFormatting.RED));
        player.sendSystemMessage(
                Component.translatable("command.tnc_extras.cape_list_hint").withStyle(ChatFormatting.GRAY));
    }

    private static int showHelp(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) {
            return 0;
        }

        player.sendSystemMessage(
                Component.translatable("command.tnc_extras.cape_help_title").withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(
                Component.translatable("command.tnc_extras.cape_help_list").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(
                Component.translatable("command.tnc_extras.cape_help_set").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(
                Component.translatable("command.tnc_extras.cape_help_current").withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(
                Component.translatable("command.tnc_extras.cape_help_reload").withStyle(ChatFormatting.YELLOW));

        return 1;
    }

    private static int listCapes(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) {
            return 0;
        }

        List<String> capes = CapeManager.getCapeNames(player.getUUID());
        int currentSelection = CapeManager.getSelectedCape(player.getUUID());

        if (capes.isEmpty()) {
            player.sendSystemMessage(
                    Component.translatable("command.tnc_extras.no_capes").withStyle(ChatFormatting.RED));
            return 0;
        }

        player.sendSystemMessage(
                Component.translatable("command.tnc_extras.cape_list_title").withStyle(ChatFormatting.GOLD));

        String vanillaIndicator = currentSelection == -1
                ? Component.translatable("command.tnc_extras.selected").getString()
                : "";
        player.sendSystemMessage(Component.translatable("command.tnc_extras.cape_list_vanilla", vanillaIndicator)
                .withStyle(ChatFormatting.GRAY));

        for (int i = 0; i < capes.size(); i++) {
            String capeId = capes.get(i);
            String displayName = CapeManager.getCapeDisplayName(capeId);
            String indicator = currentSelection == i
                    ? Component.translatable("command.tnc_extras.selected").getString()
                    : "";
            player.sendSystemMessage(
                    Component.translatable("command.tnc_extras.cape_list_entry", i, displayName, indicator)
                            .withStyle(ChatFormatting.AQUA));
        }

        player.sendSystemMessage(
                Component.translatable("command.tnc_extras.cape_list_hint").withStyle(ChatFormatting.GRAY));

        return 1;
    }

    private static int setCapeByName(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) {
            return 0;
        }

        String input = StringArgumentType.getString(context, "cape_name");
        List<String> availableCapes = CapeManager.getCapeNames(player.getUUID());

        if ("none".equalsIgnoreCase(input) || "vanilla".equalsIgnoreCase(input)) {
            CapeManager.setSelectedCape(player.getUUID(), -1);
            CapeManager.saveImmediately(player.getServer());
            player.sendSystemMessage(Component.translatable("command.tnc_extras.cape_set_vanilla")
                    .withStyle(ChatFormatting.GREEN));
            return 1;
        }

        // Try to parse as an integer index first
        try {
            int index = Integer.parseInt(input);
            if (index == -1) {
                CapeManager.setSelectedCape(player.getUUID(), -1);
                CapeManager.saveImmediately(player.getServer());
                player.sendSystemMessage(Component.translatable("command.tnc_extras.cape_set_vanilla")
                        .withStyle(ChatFormatting.GREEN));
                return 1;
            } else if (index >= 0 && index < availableCapes.size()) {
                CapeManager.setSelectedCape(player.getUUID(), index);
                CapeManager.saveImmediately(player.getServer());
                String capeId = availableCapes.get(index);
                String displayName = CapeManager.getCapeDisplayName(capeId);
                player.sendSystemMessage(Component.translatable("command.tnc_extras.cape_set", displayName)
                        .withStyle(ChatFormatting.AQUA));
                return 1;
            } else {
                player.sendSystemMessage(Component.translatable(
                                "command.tnc_extras.cape_invalid_index", index, availableCapes.size() - 1)
                        .withStyle(ChatFormatting.RED));
                return 0;
            }
        } catch (NumberFormatException e) {
            // Not a number, try as a name
        }

        // Try to match by display name or internal ID
        String capeId = CapeManager.getCapeIdFromName(input);
        if (capeId != null) {
            for (int i = 0; i < availableCapes.size(); i++) {
                if (availableCapes.get(i).equals(capeId)) {
                    CapeManager.setSelectedCape(player.getUUID(), i);
                    CapeManager.saveImmediately(player.getServer());
                    String displayName = CapeManager.getCapeDisplayName(capeId);
                    player.sendSystemMessage(Component.translatable("command.tnc_extras.cape_set", displayName)
                            .withStyle(ChatFormatting.AQUA));
                    return 1;
                }
            }
        }

        sendCapeNotFoundMessage(player, input);
        return 0;
    }

    private static int showCurrentCape(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) {
            return 0;
        }

        List<String> availableCapes = CapeManager.getCapeNames(player.getUUID());
        int currentSelection = CapeManager.getSelectedCape(player.getUUID());

        if (availableCapes.isEmpty()) {
            player.sendSystemMessage(
                    Component.translatable("command.tnc_extras.no_capes").withStyle(ChatFormatting.RED));
            return 0;
        }

        if (currentSelection == -1) {
            player.sendSystemMessage(Component.translatable("command.tnc_extras.cape_current_vanilla")
                    .withStyle(ChatFormatting.GOLD));
        } else if (currentSelection >= 0 && currentSelection < availableCapes.size()) {
            String capeId = availableCapes.get(currentSelection);
            String displayName = CapeManager.getCapeDisplayName(capeId);
            player.sendSystemMessage(Component.translatable("command.tnc_extras.cape_current", displayName)
                    .withStyle(ChatFormatting.AQUA));
        } else {
            player.sendSystemMessage(Component.translatable("command.tnc_extras.cape_invalid_selection")
                    .withStyle(ChatFormatting.RED));
        }

        return 1;
    }

    private static int reloadCapes(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayerFromContext(context);
        if (player == null) {
            return 0;
        }

        player.sendSystemMessage(
                Component.translatable("command.tnc_extras.cape_reloading").withStyle(ChatFormatting.GRAY));

        ModUtils.runAsync("CapeReload-" + player.getGameProfile().getName(), () -> {
            try {
                CapeManager.loadCapesFor(player.getUUID());

                if (player.getServer() != null) {
                    player.getServer().execute(() -> {
                        List<String> capes = CapeManager.getCapeNames(player.getUUID());
                        if (capes.isEmpty()) {
                            player.sendSystemMessage(Component.translatable("command.tnc_extras.no_capes_found")
                                    .withStyle(ChatFormatting.YELLOW));
                        } else {
                            player.sendSystemMessage(
                                    Component.translatable("command.tnc_extras.cape_reloaded", capes.size())
                                            .withStyle(ChatFormatting.GREEN));
                            player.sendSystemMessage(Component.translatable("command.tnc_extras.cape_list_hint")
                                    .withStyle(ChatFormatting.GRAY));
                        }
                    });
                }
            } catch (Exception e) {
                if (player.getServer() != null) {
                    player.getServer()
                            .execute(() -> player.sendSystemMessage(
                                    Component.translatable("command.tnc_extras.cape_reload_failed", e.getMessage())
                                            .withStyle(ChatFormatting.RED)));
                }
            }
        });

        return 1;
    }
}
