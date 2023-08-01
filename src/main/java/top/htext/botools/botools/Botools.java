package top.htext.botools.botools;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.htext.botools.botools.commands.BotoolsCommand;

public class Botools implements ModInitializer {
    public static final String VERSION_NAME = MinecraftClient.getInstance().getGame().getVersion().getName();
    public static final Logger LOGGER = LogManager.getLogger(Botools.MOD_NAME);
    public static final String MOD_NAME = "Botools";

    @Override
    public void onInitialize() {
        LOGGER.info("Initialized.");
        LOGGER.debug("Version Name: " + VERSION_NAME);

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> BotoolsCommand.register(dispatcher));
    }
}
