package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionPlayerChangeMembershipEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_LEAVE;

public class FactionLeaveCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {

        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("leave")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_LEAVE)))
                        return false;
                    final FactionPlayer fPlayer1 = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return fPlayer1.hasFaction();
                })
                .executes(c -> {
                    final ServerPlayer player = c.getSource().getPlayer();
                    final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
                    final Faction faction = fPlayer.getFaction();

                    if (fPlayer.getRole() == faction.getOwnerRole()) {
                        c.getSource().sendSystemMessage(
                                Component.literal(DatChatFormatting.TextColour.ERROR + "You cannot leave the faction when you are the owner, disband the faction with ")
                                        .append(FactionCommandUtils.wrapCommand("/f disband"))
                                        .append(DatChatFormatting.TextColour.ERROR + ", or set a new owner with ")
                                        .append(FactionCommandUtils.wrapCommand("/f players setowner <player>", "/f players setowner "))
                        );
                        return 2;
                    }

                    final FactionPlayerChangeMembershipEvent event = new FactionPlayerChangeMembershipEvent(c.getSource().source, fPlayer, null, null, FactionPlayerChangeMembershipEvent.EChangeFactionReason.LEAVE);
                    MinecraftForge.EVENT_BUS.post(event);
                    if (event.isCanceled()) return 0;

                    final Faction newFaction = event.getNewFaction();
                    final FactionRole newRole = event.getNewRole();

                    fPlayer.setFaction(newFaction != null ? newFaction.getId() : null, newRole != null ? newRole.getId() : null, FactionPlayerChangeMembershipEvent.EChangeFactionReason.LEAVE);

                    c.getSource().sendSuccess(() ->
                            Component.literal(DatChatFormatting.TextColour.INFO + "Successfully left ")
                                    .append(
                                            faction.getNameWithDescription(newFaction)
                                                    .withStyle(RelationUtil.getRelation(fPlayer, newFaction).formatting)
                                    )
                    ,false);
                    return 1;
                }).build();

        command.then(subCommand);
        command.then(FactionCommandUtils.buildRedirect("quit", subCommand));
    }
}
