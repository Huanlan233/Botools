package top.htext.botools

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

class BotCommandRegister {
	
	fun register(dispatcher: CommandDispatcher<ServerCommandSource>){
		val command = literal("bot")
			.then(literal("add")
				.then(argument("bot",StringArgumentType.word())
					.requires {it.hasPermissionLevel(3)}
					.executes{addBot(it)}
				)
			)
		dispatcher.register(command)
	}
}