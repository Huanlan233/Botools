package top.htext.botools;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class BotCommands {
    public static int addBot(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();

        String name = StringArgumentType.getString(context,"bot");
        double posX = source.getPlayer().getPos().getX();
        double posY = source.getPlayer().getPos().getY();
        double posZ = source.getPlayer().getPos().getZ();
        float rotX = source.getPlayer().getRotationClient().x;
        float rotY = source.getPlayer().getRotationClient().y;
        RegistryKey<World> dimType = source.getWorld().getRegistryKey();

        return 1;
    }
}
