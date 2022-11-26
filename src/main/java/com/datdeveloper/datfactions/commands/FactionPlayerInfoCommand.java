package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.commands.arguments.FactionPlayerArgument;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datmoddingapi.asyncTask.AsyncHandler;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
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

                            return execute(c, factionPlayer, target);
                        }))
                .executes(c -> {
                    final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());


                    return execute(c, factionPlayer, factionPlayer);
                })
                .build();

        command.then(subCommand);
        command.then(Commands.literal("pinfo").requires(predicate).redirect(subCommand));
        command.then(Commands.literal("showplayer").requires(predicate).redirect(subCommand));
    }

    static int execute(final CommandContext<CommandSourceStack> context, final FactionPlayer player, final FactionPlayer target) {
        AsyncHandler.runAsyncTask(() -> context.getSource().sendSystemMessage(target.getChatSummary(player.getFaction())));
        return 1;
    }
}
