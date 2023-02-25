package com.datdeveloper.datfactions.commands.admin;

import com.datdeveloper.datfactions.commands.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;

public class FactionAdminCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> command = LiteralArgumentBuilder.literal("factionsadmin");

        final CommandNode<CommandSourceStack> mainCommand = dispatcher.register(command);
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("fadmin").redirect(mainCommand));
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("datfactionsadmin").redirect(mainCommand));
    }
}
