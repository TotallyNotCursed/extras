package xyz.splack.tnc.extras.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xyz.splack.tnc.extras.command.tnc.LocateModOresCommand;
import xyz.splack.tnc.extras.command.tnc.UuidCommand;

public class TncCommand {
  static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    var builder = Commands.literal("tnc");
    UuidCommand.register(builder);
    LocateModOresCommand.register(builder);
    dispatcher.register(builder);
  }
}
