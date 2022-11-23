package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.commands.arguments.FactionArgument;
import com.datdeveloper.datfactions.factionData.Faction;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONINFO;

public class FactionInfoCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("info")
                .requires(FactionPermissions.hasPermission(FACTIONINFO))
                .then(Commands.argument("targetFaction", new FactionArgument())
                        .executes(c -> {
                            final Faction target = c.getArgument("targetFaction", Faction.class);

                            c.getSource().sendSystemMessage(target.getChatSummary());

                            return 1;
                        })).build();

        command.then(subCommand);
        command.then(Commands.literal("finfo").redirect(subCommand));
        command.then(Commands.literal("faction").redirect(subCommand));
        command.then(Commands.literal("show").redirect(subCommand));
    }
}
