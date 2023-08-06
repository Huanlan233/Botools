package top.htext.botools.commands;

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
import top.htext.botools.config.BotConfig;
import top.htext.botools.config.BotConfigManager;
import top.htext.botools.suggestions.BotSuggestionProvider;

import java.io.IOException;
import java.text.MessageFormat;
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
                .then(literal("modify")
                        .then(argument("name", StringArgumentType.word())
                                .suggests((context, builder) -> new BotSuggestionProvider().getSuggestions(context, builder))
                                .then(literal("info").then(argument("info", StringArgumentType.string())
                                        .executes(context -> modify(context, StringArgumentType.getString(context, "name"), StringArgumentType.getString(context, "info")))))
                                .then(literal("dimension").then(argument("dimension", DimensionArgumentType.dimension())
                                        .executes(context -> modify(context, StringArgumentType.getString(context, "name"), DimensionArgumentType.getDimensionArgument(context, "dimension")))))
                                .then(literal("rotation").then(argument("rotation", RotationArgumentType.rotation())
                                        .executes(context -> modify(context, StringArgumentType.getString(context, "name"), RotationArgumentType.getRotation(context, "rotation").toAbsoluteRotation(context.getSource())))))
                                .then(literal("pos").then(argument("pos", Vec3ArgumentType.vec3())
                                        .executes(context -> modify(context, StringArgumentType.getString(context, "name"), Vec3ArgumentType.getVec3(context, "pos")))))))
                .then(literal("info")
                        .then(argument("name", StringArgumentType.word())
                                .executes(context -> info(context, StringArgumentType.getString(context, "name")))
                                .suggests((context, builder) -> new BotSuggestionProvider().getSuggestions(context,builder))))
                .then(literal("spawn")
                        .then(argument("name", StringArgumentType.word())
                                .then(literal("use")
                                        .then(literal("continuous")
                                                .executes(context -> spawnUseContinuous(context, StringArgumentType.getString(context, "name"))))
                                        .then(literal("once")
                                                .executes(context -> spawnUseOnce(context, StringArgumentType.getString(context, "name")))))
                                .suggests(((context, builder) -> new BotSuggestionProvider().getSuggestions(context, builder)))
                                .executes(context -> spawn(context, StringArgumentType.getString(context, "name")))))
                .then(literal("list")
                        .executes(BotoolsCommand::list))
                .then(literal("remove")
                        .then(argument("name", StringArgumentType.word())
                                .suggests((context, builder) -> new BotSuggestionProvider().getSuggestions(context, builder))
                                .executes(context -> remove(context, String.valueOf(StringArgumentType.getString(context, "name"))))))
                .then(literal("add")
                        .then(argument("name", StringArgumentType.word()).executes(context -> add(context, String.valueOf(StringArgumentType.getString(context, "name")), null, null, null, null))
                                .then(argument("info", StringArgumentType.string()).executes((context -> add(context, String.valueOf(StringArgumentType.getString(context, "name")), StringArgumentType.getString(context, "info"), null,null,null)))
                                        .then((argument("pos", Vec3ArgumentType.vec3()).executes((context -> add(context, String.valueOf(StringArgumentType.getString(context, "name")), StringArgumentType.getString(context, "info"), DimensionArgumentType.getDimensionArgument(context, "dimension").getRegistryKey().getValue(), Vec3ArgumentType.getVec3(context, "pos"), RotationArgumentType.getRotation(context, "rotation").toAbsoluteRotation(context.getSource()))))
                                                        .then(argument("rotation", RotationArgumentType.rotation()).executes((context -> add(context, String.valueOf(StringArgumentType.getString(context, "name")), StringArgumentType.getString(context, "info"), DimensionArgumentType.getDimensionArgument(context, "dimension").getRegistryKey().getValue(), Vec3ArgumentType.getVec3(context, "pos"), RotationArgumentType.getRotation(context, "rotation").toAbsoluteRotation(context.getSource()))))
                                                                .then((argument("dimension", DimensionArgumentType.dimension()).executes((context -> add(context, String.valueOf(StringArgumentType.getString(context, "name")), StringArgumentType.getString(context, "info"), DimensionArgumentType.getDimensionArgument(context, "dimension").getRegistryKey().getValue(), Vec3ArgumentType.getVec3(context, "pos"), RotationArgumentType.getRotation(context, "rotation").toAbsoluteRotation(context.getSource())))))))
                                                )
                                        )
                                )
                        )
                );
        dispatcher.register(botools);
    }

    private static <T> int modify(CommandContext<ServerCommandSource> context, String name, T value) {
        try {
            if (BotConfigManager.getBotConfig(context, name) == null) {
                context.getSource().sendError(new TranslatableText("commands.botools.failed.not_exist"));
                return 0;
            }
            if (!(value instanceof Vec3d) && !(value instanceof Vec2f) && !(value instanceof String) && !(value instanceof Identifier)) {
                context.getSource().sendError(new TranslatableText("commands.botools.modify.failed.unknown_type"));
                return 0;
            }

            BotConfigManager.modifyBotConfig(context, name, value);

            context.getSource().sendFeedback(new TranslatableText("commands.botools.modify.success", name),true);

            return 1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

            context.getSource().getMinecraftServer().getCommandManager().execute(context.getSource(),
                    MessageFormat.format(
                            "/player {0} spawn at {1} {2} {3} facing {4} {5} in {6}",
                            name, posX, posY, posZ, rotY, rotX, dimension
                    )
            );

            return 1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int spawnUseContinuous(CommandContext<ServerCommandSource> context, String name){
        spawn(context, name);

        context.getSource().getMinecraftServer().getCommandManager().execute(context.getSource(),
                MessageFormat.format("/player {0} use continuous", name)
        );
        return 1;
    }

    private static int spawnUseOnce(CommandContext<ServerCommandSource> context, String name){
        spawn(context, name);

        context.getSource().getMinecraftServer().getCommandManager().execute(context.getSource(),
                MessageFormat.format("/player {0} use once", name)
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

