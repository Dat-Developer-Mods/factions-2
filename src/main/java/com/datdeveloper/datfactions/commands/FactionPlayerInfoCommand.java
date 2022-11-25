package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.commands.arguments.FactionArgument;
import com.datdeveloper.datfactions.commands.arguments.FactionPlayerArgument;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;

import java.util.Collection;
import java.util.function.Predicate;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONINFO;

public class FactionPlayerInfoCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final Predicate<CommandSourceStack> predicate = FactionPermissions.hasPermission(FACTIONINFO);
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("player")
                .requires(predicate)
                .then(Commands.argument("targetPlayer", new FactionPlayerArgument())
                        .executes(c -> {
                            final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                            final FactionPlayer target = c.getArgument("targetPlayer", FactionPlayer.class);

                            c.getSource().sendSystemMessage(target.getChatDescription(factionPlayer.getFaction()));

                            return 1;
                        }))
                .executes(c -> {
                    final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());

                    if (factionPlayer == null || !factionPlayer.hasFaction()) return 2;

                    c.getSource().sendSystemMessage(factionPlayer.getFaction().getChatSummary(factionPlayer.getFaction()));

                    return 1;
                })
                .build();

        command.then(subCommand);
        command.then(Commands.literal("pinfo").requires(predicate).redirect(subCommand));
    }
}
