package top.htext.botools;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Botools implements ModInitializer {
    @Override
    public void onInitialize() {
        logInfo("Mod load initiated.");

        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            BotCommandsRegister.register(dispatcher);
        }));
    }

    private final Logger logger = LoggerFactory.getLogger("botools");
    public void logInfo(String message) {logger.info(message);}
}


