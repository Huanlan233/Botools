package top.htext.botools;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BotCommandsRegister {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> commands = literal("bot")
                .then(literal("add")
                        .then(argument("name", StringArgumentType.word()))
                        .requires((source -> source.hasPermissionLevel(3)))
                        .executes((BotCommands::addBot))
                );

        dispatcher.register(commands);
    }
}
