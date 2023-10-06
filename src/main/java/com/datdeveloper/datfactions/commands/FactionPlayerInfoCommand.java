package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.commands.suggestions.DatSuggestionProviders;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datmoddingapi.concurrentTask.ConcurrentHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_PLAYER_INFO;

/**
 * A command to allow a player to view info about other players
 */
public class FactionPlayerInfoCommand {
    /** An argument for the player being targeted */
    static final String TARGET_PLAYER_ARG = "Target Player";

    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("playerinfo")
                .requires(FactionPermissions.hasPermission(FACTION_PLAYER_INFO))
                .then(Commands.argument(TARGET_PLAYER_ARG, GameProfileArgument.gameProfile())
                        .suggests(DatSuggestionProviders.fPlayerProvider)
                        .executes(c -> executeTarget(c, GameProfileArgument.getGameProfiles(c, TARGET_PLAYER_ARG)
                                .stream().findFirst().get())))
                .executes(FactionPlayerInfoCommand::executeSelf)
                .build();

        command.then(subCommand);
        command.then(FactionCommandUtils.buildRedirect("pinfo", subCommand));
        command.then(FactionCommandUtils.buildRedirect("showplayer", subCommand));
    }

    /**
     * Handle getting info about the player's self
     * @param c The command context
     * @return 1 for success
     */
    private static int executeSelf(final CommandContext<CommandSourceStack> c) {
        if (!c.getSource().isPlayer()) {
            throw new CommandRuntimeException(Component.literal("You must provide a player to get info about"));
        }

        final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());

        return sendPlayerInfo(c, factionPlayer, factionPlayer);
    }

    /**
     * Handle getting info about another player
     * @param c The command context
     * @return 1 if successful
     */
    private static int executeTarget(final CommandContext<CommandSourceStack> c, final GameProfile profile) {
        final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
        final FactionPlayer target = FPlayerCollection.getInstance().getByKey(profile.getId());

        return sendPlayerInfo(c, factionPlayer, target);
    }

    static int sendPlayerInfo(final CommandContext<CommandSourceStack> context, final FactionPlayer player, final FactionPlayer target) {
        ConcurrentHandler.runConcurrentTask(() -> context.getSource().sendSystemMessage(target.getChatSummary(player.getFaction())));
        return 1;
    }
}
