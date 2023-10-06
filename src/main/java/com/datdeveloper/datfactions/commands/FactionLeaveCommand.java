package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionPlayerChangeMembershipEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_LEAVE;

/**
 * A command that allows a player to leave a faction
 */
public class FactionLeaveCommand {
    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("leave")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_LEAVE)))
                        return false;

                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return fPlayer.hasFaction();
                })
                .executes(FactionLeaveCommand::run).build();

        command.then(subCommand);
        command.then(FactionCommandUtils.buildRedirect("quit", subCommand));
    }

    /**
     * Execute the leave command
     * @param c The command context
     * @return 1 if successful
     */
    private static int run(final CommandContext<CommandSourceStack> c) {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
        final Faction faction = fPlayer.getFaction();


        final FactionPlayerChangeMembershipEvent.Pre event = new FactionPlayerChangeMembershipEvent.Pre(c.getSource().getPlayer(),
                fPlayer,
                null,
                null,
                FactionPlayerChangeMembershipEvent.EChangeFactionReason.LEAVE);
        MinecraftForge.EVENT_BUS.post(event);

        final Faction newFaction = event.getNewFaction();
        final FactionRole newRole = event.getNewRole();
        final Event.Result result = event.getResult();

        if (result == Event.Result.DENY) {
            final Component reason = event.getDenyReason();
            if (reason != null) {
                throw new CommandRuntimeException(reason);
            } else {
                return 0;
            }
        } else if (result == Event.Result.DEFAULT && fPlayer.getRole() == faction.getOwnerRole()) {
            throw new CommandRuntimeException(
                    Component.literal(DatChatFormatting.TextColour.ERROR + "You cannot leave the faction when you are the owner, disband the faction with ")
                            .append(FactionCommandUtils.wrapCommand("/f disband"))
                            .append(DatChatFormatting.TextColour.ERROR + ", or set a new owner with ")
                            .append(FactionCommandUtils.wrapCommand("/f players setowner <player>", "/f players setowner "))
            );
        }

        fPlayer.setFaction(newFaction, newRole, FactionPlayerChangeMembershipEvent.EChangeFactionReason.LEAVE);

        c.getSource().sendSuccess(() ->
                        Component.literal(DatChatFormatting.TextColour.INFO + "Successfully left ")
                                .append(
                                        faction.getNameWithDescription(newFaction)
                                                .withStyle(RelationUtil.getRelation(fPlayer, faction).formatting)
                                )
                , false);
        return 1;
    }
}
