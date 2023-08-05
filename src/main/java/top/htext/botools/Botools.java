package top.htext.botools;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.htext.botools.commands.BotoolsCommand;

public class Botools implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(Botools.MOD_NAME);
    public static final String MOD_NAME = "Botools";

    @Override
    public void onInitialize() {
        LOGGER.info("Botools initialized.");

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> BotoolsCommand.register(dispatcher));
    }
}
