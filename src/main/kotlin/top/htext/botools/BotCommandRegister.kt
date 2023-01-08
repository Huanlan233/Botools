package top.htext.botools

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

class BotCommandRegister {
	
	fun register(dispatcher: CommandDispatcher<ServerCommandSource>){
		val command = literal("bot")
			.executes {BotoolsCommands().bot(it)}
			.then(literal("add")
				.executes{BotoolsCommands().addBot(it)}
			)
		dispatcher.register(command)
	}
}