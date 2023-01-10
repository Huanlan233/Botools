package top.htext.botools

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText

fun addBot(context: CommandContext<ServerCommandSource>): Int{
	val source = context.source
	
	val name = StringArgumentType.getString(context,"bot")
	val posX = source.player.pos.x
	val posY = source.player.pos.y
	val posZ = source.player.pos.z
	val rotX = source.player.rotationClient.x
	val rotY = source.player.rotationClient.y
	val dimType = source.world.registryKey
	
	source.sendFeedback(LiteralText("Name:${name}"),false)
	source.sendFeedback(LiteralText("Pos:[X:${posX},Y:${posY},Z:${posZ}]"),false)
	source.sendFeedback(LiteralText("Rot:[X:${rotX},Y:${rotY}"),false)
	source.sendFeedback(LiteralText("Dim:${dimType}"),false)
	
	return 1
}