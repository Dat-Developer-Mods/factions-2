package com.datdeveloper.datfactions.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FactionsCommand {
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = LiteralArgumentBuilder.<CommandSourceStack>literal("factions")
                .then(FactionCreateCommand.register());

        CommandNode<CommandSourceStack> mainCommand = dispatcher.register(command);
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("f").redirect(mainCommand));
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("datfactions").redirect(mainCommand));
    }
}
