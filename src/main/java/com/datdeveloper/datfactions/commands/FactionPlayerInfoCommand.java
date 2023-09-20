package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.commands.suggestions.DatSuggestionProviders;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datmoddingapi.concurrentTask.ConcurrentHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_PLAYER_INFO;

public class FactionPlayerInfoCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("playerinfo")
                .requires(FactionPermissions.hasPermission(FACTION_PLAYER_INFO))
                .then(Commands.argument("Target Player", GameProfileArgument.gameProfile())
                        .suggests(DatSuggestionProviders.fPlayerProvider)
                        .executes(c -> {
                            final GameProfile profile = GameProfileArgument.getGameProfiles(c, "Target Player")
                                    .stream().findFirst().orElse(null);
                            if (profile == null) {
                                c.getSource().sendFailure(Component.literal("Failed to find player"));
                                return 2;
                            }

                            final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                            final FactionPlayer target = FPlayerCollection.getInstance().getByKey(profile.getId());

                            return execute(c, factionPlayer, target);
                        }))
                .executes(c -> {
                    final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());

                    return execute(c, factionPlayer, factionPlayer);
                })
                .build();

        command.then(subCommand);
        command.then(FactionCommandUtils.buildRedirect("pinfo", subCommand));
        command.then(FactionCommandUtils.buildRedirect("showplayer", subCommand));
    }

    static int execute(final CommandContext<CommandSourceStack> context, final FactionPlayer player, final FactionPlayer target) {
        ConcurrentHandler.runConcurrentTask(() -> context.getSource().sendSystemMessage(target.getChatSummary(player.getFaction())));
        return 1;
    }
}
