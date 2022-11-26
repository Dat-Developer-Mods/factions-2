package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.commands.arguments.FactionPlayerArgument;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.function.Predicate;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONPLAYERINFO;

public class FactionPlayerInfoCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final Predicate<CommandSourceStack> predicate = FactionPermissions.hasPermission(FACTIONPLAYERINFO);
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("player")
                .requires(predicate)
                .then(Commands.argument("targetPlayer", new FactionPlayerArgument())
                        .executes(c -> {
                            final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                            final FactionPlayer target = c.getArgument("targetPlayer", FactionPlayer.class);

                            c.getSource().sendSystemMessage(target.getChatSummary(factionPlayer.getFaction()));

                            return 1;
                        }))
                .executes(c -> {
                    final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());

                    c.getSource().sendSystemMessage(factionPlayer.getChatSummary(factionPlayer.getFaction()));

                    return 1;
                })
                .build();

        command.then(subCommand);
        command.then(Commands.literal("pinfo").requires(predicate).redirect(subCommand));
        command.then(Commands.literal("showplayer").requires(predicate).redirect(subCommand));
    }
}
