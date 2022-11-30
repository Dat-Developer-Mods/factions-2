package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONPLAYERINFO;

public class FactionJoinCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final Predicate<CommandSourceStack> predicate = FactionPermissions.hasPermission(FACTIONPLAYERINFO);
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("join")
                .requires(predicate)
                .then(Commands.argument("targetFaction", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            // Get factions
                            return builder.buildFuture();
                        })
                        .executes(c -> {
                            final String targetName = c.getArgument("targetFaction", String.class);
                            final Faction target = FactionCollection.getInstance().getByName(targetName);
                            if (target == null) {
                                c.getSource().sendFailure(Component.literal("Cannot find a faction with that name"));
                                return 2;
                            }
                            final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());



                            return 0;
                        }))
                .build();

        command.then(subCommand);
    }
}
