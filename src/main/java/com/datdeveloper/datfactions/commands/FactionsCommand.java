package com.datdeveloper.datfactions.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FactionsCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> command = LiteralArgumentBuilder.literal("factions");
        FactionListCommand.register(command);
        FactionInfoCommand.register(command);
        FactionPlayerInfoCommand.register(command);
        FactionMapCommand.register(command);
        FactionPlayerInvitesCommand.register(command);
        FactionCreateCommand.register(command);

        FactionJoinCommand.register(command);
        FactionLeaveCommand.register(command);
        FactionHomeCommand.register(command);
        FactionChatCommand.register(command);

        FactionNameCommand.register(command);
        FactionDescriptionCommand.register(command);
        FactionMotdCommand.register(command);
        FactionSetHomeCommand.register(command);
        FactionDisbandCommand.register(command);

        FactionRoleCommand.register(command);
        FactionInvitesCommand.register(command);
        FactionRelationCommand.register(command);
        FactionPlayersCommand.register(command);
        FactionFlagsCommand.register(command);

        FactionClaimCommand.register(command);
        FactionUnclaimCommand.register(command);

        final CommandNode<CommandSourceStack> mainCommand = dispatcher.register(command);
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("f").redirect(mainCommand));
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("datfactions").redirect(mainCommand));
    }
}
