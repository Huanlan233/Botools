package top.htext.botools

import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText

class BotoolsCommands {
	fun bot(context: CommandContext<ServerCommandSource>): Int{
		context.source.sendFeedback(TranslatableText("botools.info"),false)
		return 1
	}
	
	fun addBot(context: CommandContext<ServerCommandSource>): Int{
		val path = context.source.server.runDirectory.toPath().toAbsolutePath()
		context.source.sendFeedback(LiteralText(path.toString()),false)
		
		return 1
	}
}