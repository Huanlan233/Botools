package top.htext.botools.botools.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BotConfigManager {
    private static final Gson gson = new Gson();
    private static final TypeToken<List<BotConfig>> botConfigsTypeToken = new TypeToken<>() {};

    private static List<BotConfig> deserializeBotConfigFile(File botConfigFile) throws IOException {
        return gson.fromJson(Files.readString(Path.of(botConfigFile.toURI())), botConfigsTypeToken.getType());
    }

    private static String serializeBotConfigFile(List<BotConfig> botConfigList) {
        return gson.toJson(botConfigList);
    }

    private static File getBotConfigFile(CommandContext<ServerCommandSource> context) {
        return new File(context.getSource().getServer().getSavePath(WorldSavePath.ROOT).toAbsolutePath() + "botools.json");
    }

    public static List<BotConfig> getBotConfigList(CommandContext<ServerCommandSource> context) throws IOException {
        File botConfigFile = getBotConfigFile(context);

        if (!botConfigFile.exists()) botConfigFile.createNewFile();

        if (deserializeBotConfigFile(botConfigFile) == null) return new ArrayList<>();

        return deserializeBotConfigFile(botConfigFile);
    }

    public static BotConfig getBotConfig(CommandContext<ServerCommandSource> context, String name) throws IOException {

        List<BotConfig> botConfigList = getBotConfigList(context);
        for ( BotConfig botConfig : botConfigList ) if (Objects.equals(botConfig.getName(), name)) return botConfig;
        return null;
    }

    public static void addBotConfig(CommandContext<ServerCommandSource> context, BotConfig botConfig) throws IOException {
        List<BotConfig> botConfigList = getBotConfigList(context);

        botConfigList.add(botConfig);
        Files.writeString(Path.of(getBotConfigFile(context).toURI()), serializeBotConfigFile(botConfigList));
    }

    public static void removeBotConfig(CommandContext<ServerCommandSource> context, BotConfig botConfig) throws IOException {

        List<BotConfig> botConfigList = getBotConfigList(context);
        botConfigList.removeIf(config -> config.getName().equals(botConfig.getName()));
        Files.writeString(Path.of(getBotConfigFile(context).toURI()), serializeBotConfigFile(botConfigList));
    }
}