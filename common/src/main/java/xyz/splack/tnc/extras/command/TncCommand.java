package xyz.splack.tnc.extras.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xyz.splack.tnc.extras.command.tnc.CapeCommand;
import xyz.splack.tnc.extras.command.tnc.LocateModOresCommand;

public class TncCommand {
    static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var builder = Commands.literal("tnc");
        LocateModOresCommand.register(builder);
        CapeCommand.register(builder);
        dispatcher.register(builder);
    }
}
