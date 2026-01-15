package top.wmsnp.caps.server;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import top.wmsnp.caps.common.ServerConfig;

public class ModCommands {
    public static int setMaxCommand(CommandContext<CommandSourceStack> context){
        int newValue = IntegerArgumentType.getInteger(context, "value");
        ServerConfig.SERVER_MAX_VEIN_BLOCKS.set(newValue);
        ServerConfig.SERVER_MAX_VEIN_BLOCKS.save();
        context.getSource().sendSuccess(() -> Component.translatable("caps.command.server_max_vein_blocks", newValue), true);
        return 1;
    }
}