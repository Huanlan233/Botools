package top.htext.botools.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

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

    private static List<BotConfig> deserializer(File botConfigFile) throws IOException {
        return gson.fromJson(Files.readString(Path.of(botConfigFile.toURI())), botConfigsTypeToken.getType());
    }

    private static String serializer(List<BotConfig> botConfigList) {
        return gson.toJson(botConfigList);
    }

    private static File getBotConfigFile(CommandContext<ServerCommandSource> context) throws IOException {
        File dir = new File(MinecraftClient.getInstance().runDirectory + "/config/botools");
        File file = new File(dir + "/" + context.getSource().getServer().getSaveProperties().getLevelName() + ".json");

        if (!dir.exists()) dir.mkdirs();
        if (!file.exists()) file.createNewFile();

        return file;
    }

    public static List<BotConfig> getBotConfigList(CommandContext<ServerCommandSource> context) throws IOException {
        File botConfigFile = getBotConfigFile(context);
        if (deserializer(botConfigFile) == null) return new ArrayList<>();
        return deserializer(botConfigFile);
    }

    public static BotConfig getBotConfig(CommandContext<ServerCommandSource> context, String name) throws IOException {
        List<BotConfig> botConfigList = getBotConfigList(context);
        for ( BotConfig botConfig : botConfigList ) if (Objects.equals(botConfig.getName(), name)) return botConfig;
        return null;
    }

    public static void addBotConfig(CommandContext<ServerCommandSource> context, BotConfig botConfig) throws IOException {
        List<BotConfig> botConfigList = getBotConfigList(context);
        botConfigList.add(botConfig);
        Files.writeString(Path.of(getBotConfigFile(context).toURI()), serializer(botConfigList));
    }

    public static void removeBotConfig(CommandContext<ServerCommandSource> context, BotConfig botConfig) throws IOException {
        List<BotConfig> botConfigList = getBotConfigList(context);
        botConfigList.removeIf(config -> config.getName().equals(botConfig.getName()));
        Files.writeString(Path.of(getBotConfigFile(context).toURI()), serializer(botConfigList));
    }

    public static <T> void modifyBotConfig(CommandContext<ServerCommandSource> context, String name, T value) throws IOException {
        List<BotConfig> botConfigList = getBotConfigList(context);

        botConfigList.stream()
                .filter(config -> config.getName().equals(name))
                .findFirst()
                .ifPresent(config -> {
                    if (value instanceof Vec3d) config.setPos((Vec3d) value);
                    if (value instanceof Vec2f) config.setRotation((Vec2f) value);
                    if (value instanceof String) config.setInfo((String) value);
                    if (value instanceof Identifier) config.setDimension((Identifier) value);
                });
        Files.writeString(Path.of(getBotConfigFile(context).toURI()), serializer(botConfigList));
    }
}
