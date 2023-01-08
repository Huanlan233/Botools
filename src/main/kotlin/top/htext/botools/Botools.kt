package top.htext.botools

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Botools: ModInitializer {
	
	override fun onInitialize() {
		logInfo("Mod load Initiated.")
		
		CommandRegistrationCallback.EVENT.register {dispatcher,dedicated->
			BotCommandRegister().register(dispatcher)
		}
	}
}

private val logger: Logger = LoggerFactory.getLogger("Botools")

fun logInfo(message: String){ logger.info(message) }