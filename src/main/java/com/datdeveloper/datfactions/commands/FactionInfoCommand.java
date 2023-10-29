package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.commands.suggestions.DatSuggestionProviders;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionCollection;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datmoddingapi.concurrentTask.ConcurrentHandler;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_INFO;

/**
 * A command that shows a player info about factions
 */
public class FactionInfoCommand {
    /** The argument for the faction target */
    static final String TARGET_FACTION_ARG = "Target Faction";

    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("info")
                .requires(DatPermissions.hasPermission(FACTION_INFO))
                .then(Commands.argument(TARGET_FACTION_ARG, StringArgumentType.word())
                        .suggests(DatSuggestionProviders.factionProvider)
                        .executes(c -> executeTarget(c, c.getArgument(TARGET_FACTION_ARG, String.class))))
                .executes(FactionInfoCommand::executeSelf)
                .build();

        command.then(subCommand);
        command.then(FactionCommandUtils.buildRedirect("faction", subCommand));
        command.then(FactionCommandUtils.buildRedirect("show", subCommand));
    }

    /**
     * Handle getting faction info about self
     * @param c The command context
     * @return 1 for success
     */
    private static int executeSelf(final CommandContext<CommandSourceStack> c) {
        final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());

        if (factionPlayer == null || !factionPlayer.hasFaction()) {
            throw new CommandRuntimeException(Component.literal("You must be in a faction get your own faction's info"));
        }

        return sendFactionInfo(c, factionPlayer, factionPlayer.getFaction());
    }

    /**
     * Handle getting faction info about a specific faction
     * @param c The command context
     * @param targetFactionName The name of the faction
     * @return 1 for success
     */
    private static int executeTarget(final CommandContext<CommandSourceStack> c, final String targetFactionName) {
        final Faction target = FactionCollection.getInstance().getByName(targetFactionName);
        if (target == null) {
            throw new CommandRuntimeException(Component.literal("Cannot find a faction with that name"));
        }

        final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());

        return sendFactionInfo(c, factionPlayer, target);
    }


    /**
     * Send the info to the player
     * @param context The command context
     * @param from The faction player that is getting the info
     * @param targetFaction The faction to get information about
     * @return 1 for success
     */
    static int sendFactionInfo(final CommandContext<CommandSourceStack> context, final FactionPlayer from, final Faction targetFaction) {
        // There's no threading protections around this, but since we're only reading it'll probably be fine
        ConcurrentHandler.runConcurrentTask(() -> context.getSource().sendSystemMessage(targetFaction.getChatSummary(from.getFaction())));
        return 1;
    }
}
