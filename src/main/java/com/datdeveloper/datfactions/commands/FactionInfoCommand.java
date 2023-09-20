package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.commands.suggestions.DatSuggestionProviders;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datmoddingapi.concurrentTask.ConcurrentHandler;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_INFO;

public class FactionInfoCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("info")
                .requires(FactionPermissions.hasPermission(FACTION_INFO))
                .then(Commands.argument("Target Faction", StringArgumentType.word())
                        .suggests(DatSuggestionProviders.factionProvider)
                        .executes(c -> {
                            final String targetName = c.getArgument("Target Faction", String.class);
                            final Faction target = FactionCollection.getInstance().getByName(targetName);
                            if (target == null) {
                                c.getSource().sendFailure(Component.literal("Cannot find a faction with that name"));
                                return 2;
                            }

                            final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());

                            return execute(c, factionPlayer, target);
                        }))
                .executes(c -> {
                    final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                    if (factionPlayer == null || !factionPlayer.hasFaction()) {
                        c.getSource().sendFailure(Component.literal("You must be in a faction get your own faction's info"));
                        return 2;
                    }

                    return execute(c, factionPlayer, factionPlayer.getFaction());
                })
                .build();

        command.then(subCommand);
        command.then(FactionCommandUtils.buildRedirect("faction", subCommand));
        command.then(FactionCommandUtils.buildRedirect("show", subCommand));
    }

    static int execute(final CommandContext<CommandSourceStack> context, final FactionPlayer from, final Faction targetFaction) {
        ConcurrentHandler.runConcurrentTask(() -> context.getSource().sendSystemMessage(targetFaction.getChatSummary(from.getFaction())));
        return 1;
    }
}
