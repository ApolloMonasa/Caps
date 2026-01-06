package top.wmsnp.caps.server;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import top.wmsnp.caps.Caps;
import top.wmsnp.caps.common.CapsConfig;

@EventBusSubscriber(modid = "caps")
public class ModCommands {
    public static final LiteralArgumentBuilder<CommandSourceStack> GAME_MASTER = Commands.literal("caps").requires(s -> s.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER));

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(GAME_MASTER
                .then(Commands.literal("set").then(Commands.literal("max")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0, Caps.MAX_MAX_VEIN_BLOCKS))
                                .executes(context -> {
                                    int newValue = IntegerArgumentType.getInteger(context, "value");
                                    CapsConfig.SERVER_MAX_VEIN_BLOCKS.set(newValue);
                                    CapsConfig.SERVER_MAX_VEIN_BLOCKS.save();
                                    context.getSource().sendSuccess(() -> Component.translatable("caps.command.server_max_vein_blocks", newValue), true);
                                    return 1;
                                })
                        )
                ))
        );
    }
}