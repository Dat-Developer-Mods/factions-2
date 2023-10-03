package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionDisbandEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factiondata.*;
import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
import com.datdeveloper.datfactions.factiondata.relations.EFactionRelation;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_DISBAND;

/**
 * A command that allows a player to disband their faction
 */
public class FactionDisbandCommand {
    /** The argument for being sure of disbanding the faction */
    static final String SURE_ARG = "Are you sure?";

    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        command.then(Commands.literal("disband")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_DISBAND)))
                        return false;
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    final Faction faction = fPlayer.getFaction();
                    return faction != null && !faction.hasFlag(EFactionFlags.PERMANENT) && fPlayer.getRole().hasPermission(ERolePermissions.DISBAND);
                })
                .then(
                        Commands.argument(SURE_ARG, BoolArgumentType.bool())
                                .executes(c -> runSure(c, c.getArgument(SURE_ARG, Boolean.class)))
                )
                .executes(FactionDisbandCommand::runCheck).build());
    }

    /**
     * Handle when the player is sure they want to disband their faction
     * @param c The command context
     * @param areYouSure Whether the player is sure
     * @return 1 if successful
     */
    private static int runSure(final CommandContext<CommandSourceStack> c, final boolean areYouSure) {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
        final Faction faction = fPlayer.getFaction();
        if (!areYouSure) {
            throw new CommandRuntimeException(Component.literal("Faction will not be disbanded"));
        }

        final FactionDisbandEvent.Pre event = new FactionDisbandEvent.Pre(c.getSource().getPlayer(), faction);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            final Component reason = event.getDenyReason();
            if (reason != null) {
                throw new CommandRuntimeException(reason);
            } else {
                return 0;
            }
        }

        FactionCollection.getInstance().disbandFaction(faction.getId());
        return 1;
    }

    /**
     * Handle when the player hasn't confirmed they want to disband their faction
     * @param c The command context
     * @return 1 for success
     */
    private static int runCheck(final CommandContext<CommandSourceStack> c) {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
        final Faction faction = fPlayer.getFaction();

        c.getSource().sendSuccess(() -> Component.literal(
                                DatChatFormatting.TextColour.ERROR + "Are you sure you want to disband ")
                        .append(
                                faction.getNameWithDescription(faction)
                                        .withStyle(EFactionRelation.SELF.formatting)
                        )
                        .append(DatChatFormatting.TextColour.ERROR + "? This action is not reversible\n")
                        .append(DatChatFormatting.TextColour.ERROR + "Use ")
                        .append(FactionCommandUtils.wrapCommand("/f disband true", "/f disband "))
                        .append(DatChatFormatting.TextColour.ERROR + " if you are")

                , false);
        return 1;
    }
}
