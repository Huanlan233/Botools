package top.htext.botools.botools.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import top.htext.botools.botools.config.BotConfig;
import top.htext.botools.botools.config.BotConfigManager;
import top.htext.botools.botools.suggestions.BotSuggestionProvider;

import java.io.IOException;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BotoolsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> botools = CommandManager.literal("botools")
                .executes(context -> {
                    context.getSource().sendFeedback(new TranslatableText("commands.botools.help"),false);
                    return 1;
                })
                .then(literal("info")
                        .then(argument("name", StringArgumentType.word())
                                .executes(context -> BotoolsCommand.info(context, StringArgumentType.getString(context, "name")))
                                .suggests((context, builder) -> new BotSuggestionProvider().getSuggestions(context,builder))))
                .then(literal("spawn")
                        .then(argument("name", StringArgumentType.word())
                                .then(literal("use")
                                        .then(literal("continuous")
                                                .executes(context -> BotoolsCommand.spawnUseContinuous(context, StringArgumentType.getString(context, "name"))))
                                        .then(literal("once")
                                                .executes(context -> BotoolsCommand.spawnUseOnce(context, StringArgumentType.getString(context, "name")))))
                                .suggests(((context, builder) -> new BotSuggestionProvider().getSuggestions(context, builder)))
                                .executes(context -> BotoolsCommand.spawn(context, StringArgumentType.getString(context, "name")))))
                .then(literal("list")
                        .executes(BotoolsCommand::list))
                .then(literal("remove")
                        .then(argument("name", StringArgumentType.word())
                                .suggests((context, builder) -> new BotSuggestionProvider().getSuggestions(context, builder))
                                .executes(context -> BotoolsCommand.remove(context, String.valueOf(StringArgumentType.getString(context, "name"))))))
                .then(literal("add")
                        .then(argument("name", StringArgumentType.word()).executes(context -> BotoolsCommand.add(context, String.valueOf(StringArgumentType.getString(context, "name")), null, null, null, null))
                                .then(argument("info", StringArgumentType.string()).executes((context -> BotoolsCommand.add(context, String.valueOf(StringArgumentType.getString(context, "name")), StringArgumentType.getString(context, "info"), null,null,null)))
                                        .then((argument("dimension", DimensionArgumentType.dimension()).executes((context -> BotoolsCommand.add(context, String.valueOf(StringArgumentType.getString(context, "name")), StringArgumentType.getString(context, "info"), DimensionArgumentType.getDimensionArgument(context, "dimension").getRegistryKey().getValue(), null, null))))
                                                .then(argument("pos", Vec3ArgumentType.vec3()).executes((context -> BotoolsCommand.add(context, String.valueOf(StringArgumentType.getString(context, "name")), StringArgumentType.getString(context, "info"), DimensionArgumentType.getDimensionArgument(context, "dimension").getRegistryKey().getValue(), Vec3ArgumentType.getPosArgument(context, "pos").toAbsolutePos(context.getSource()), null)))
                                                        .then(argument("rotation", RotationArgumentType.rotation()).executes((context -> BotoolsCommand.add(context, String.valueOf(StringArgumentType.getString(context, "name")), StringArgumentType.getString(context, "info"), DimensionArgumentType.getDimensionArgument(context, "dimension").getRegistryKey().getValue(), Vec3ArgumentType.getPosArgument(context, "pos").toAbsolutePos(context.getSource()), RotationArgumentType.getRotation(context, "rotation").toAbsoluteRotation(context.getSource()))))
                                                ))))));
        dispatcher.register(botools);
    }

    private static int info(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        try {
            BotConfig botConfig = BotConfigManager.getBotConfig(context, name);
            if (botConfig == null) {
                context.getSource().sendError(new TranslatableText("commands.botools.failed.not_exist"));
                return 0;
            }

            context.getSource().getPlayer().sendMessage((new TranslatableText(
                    "commands.botools.info",
                    botConfig.getName(),
                    botConfig.getPos().getX(), botConfig.getPos().getY(), botConfig.getPos().getZ(),
                    botConfig.getRotation().y, botConfig.getRotation().x,
                    botConfig.getDimension(),
                    botConfig.getInfo()
            )), false);

            return 1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int list(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            List<BotConfig> botConfigList = BotConfigManager.getBotConfigList(context);

            for ( BotConfig botConfig : botConfigList ){
                context.getSource().getPlayer().sendMessage((new TranslatableText(
                        "commands.botools.list",
                        botConfig.getName(),
                        botConfig.getPos().getX(), botConfig.getPos().getY(), botConfig.getPos().getZ(),
                        botConfig.getDimension(),
                        botConfig.getInfo()
                )), false);
            }
            return 1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int spawn(CommandContext<ServerCommandSource> context, String name) {
        try {
            BotConfig botConfig = BotConfigManager.getBotConfig(context, name);

            if (botConfig == null) {
                context.getSource().sendError(new TranslatableText("commands.botools.failed.not_exist"));
                return 0;
            }

            String posX = String.valueOf(botConfig.getPos().getX());
            String posY = String.valueOf(botConfig.getPos().getY());
            String posZ = String.valueOf(botConfig.getPos().getZ());

            String rotX = String.valueOf(botConfig.getRotation().x);
            String rotY = String.valueOf(botConfig.getRotation().y);

            String dimension = botConfig.getDimension().toString();

            context.getSource().getServer().getCommandManager().execute(context.getSource(),
                    "/player " + name + " spawn at " + posX + " " + posY + " " + posZ + " facing " + rotY + " " + rotX + " in " + dimension
            );

            return 1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int spawnUseContinuous(CommandContext<ServerCommandSource> context, String name){
        spawn(context, name);

        context.getSource().getServer().getCommandManager().execute(context.getSource(),
                "/player " + name + " use continuous"
        );
        return 1;
    }

    private static int spawnUseOnce(CommandContext<ServerCommandSource> context, String name){
        spawn(context, name);

        context.getSource().getServer().getCommandManager().execute(context.getSource(),
                "/player " + name + " use once"
        );
        return 1;
    }

    private static int remove(CommandContext<ServerCommandSource> context, String name) {
        try {
            BotConfig botConfig = BotConfigManager.getBotConfig(context, name); // This will be removed

            if (botConfig == null) {
                context.getSource().sendError(new TranslatableText("commands.botools.failed.not_exist"));
                return 0;
            }

            BotConfigManager.removeBotConfig(context, botConfig);

            context.getSource().sendFeedback(new TranslatableText("commands.botools.remove_bot.success", name), true);

            return 1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int add(CommandContext<ServerCommandSource> context, String name, String info, Identifier dimension, Vec3d pos, Vec2f rotation) throws CommandSyntaxException {
        if (info == null) info = "";
        if (dimension == null)  dimension = context.getSource().getPlayer().getEntityWorld().getRegistryKey().getValue();
        if (pos == null) pos = context.getSource().getPlayer().getPos();
        if (rotation == null) rotation = context.getSource().getPlayer().getRotationClient();

        try {
            BotConfig botConfig = new BotConfig(name, pos, rotation, dimension, info); // This will be added.

            if (BotConfigManager.getBotConfig(context, botConfig.getName()) != null) {
                context.getSource().sendError(new TranslatableText("commands.botools.failed.exist"));
                return 0;
            }

            BotConfigManager.addBotConfig(context, botConfig);

            context.getSource().sendFeedback(new TranslatableText("commands.botools.add_bot.success", name, pos.getX(), pos.getY(), pos.getZ()), true);
            return 1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

