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

import java.util.function.Predicate;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONLEAVE;

public class FactionLeaveCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final Predicate<CommandSourceStack> predicate = commandSourceStack -> {
            if (!(commandSourceStack.isPlayer()) && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTIONLEAVE))
                return false;
            final FactionPlayer fPlayer = getPlayerOrTemplate(commandSourceStack.getPlayer());
            return fPlayer.hasFaction();
        };

        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("leave")
                .requires(predicate)
                .executes(c -> {
                    final ServerPlayer player = c.getSource().getPlayer();
                    final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
                    final Faction faction = fPlayer.getFaction();

                    if (fPlayer.getRole() == faction.getOwnerRole()) {
                        c.getSource().sendSystemMessage(
                                Component.literal(DatChatFormatting.TextColour.ERROR + "You cannot leave the faction when you are the owner, disband the faction with ")
                                        .append(FactionCommandUtils.wrapCommand("/f disband"))
                                        .append(DatChatFormatting.TextColour.ERROR + ", or set a new owner with ")
                                        .append(FactionCommandUtils.wrapCommand("/f setowner <player>", "/f setowner "))
                        );
                        return 2;
                    }

                    final FactionPlayerChangeMembershipEvent event = new FactionPlayerChangeMembershipEvent(c.getSource().source, fPlayer, null, null, FactionPlayerChangeMembershipEvent.EChangeFactionReason.LEAVE);
                    MinecraftForge.EVENT_BUS.post(event);
                    if (event.isCanceled()) return 0;

                    final Faction newFaction = event.getNewFaction();
                    final FactionRole newRole = event.getNewRole();

                    fPlayer.setFaction(newFaction != null ? newFaction.getId() : null, newRole != null ? newRole.getId() : null, FactionPlayerChangeMembershipEvent.EChangeFactionReason.LEAVE);

                    c.getSource().sendSuccess(
                            Component.literal(DatChatFormatting.TextColour.INFO + "Successfully left ")
                                    .append(
                                            faction.getNameWithDescription(newFaction)
                                                    .withStyle(RelationUtil.getRelation(fPlayer, newFaction).formatting)
                                    )
                    ,true);
                    return 1;
                }).build();

        command.then(subCommand);
        command.then(buildRedirect("quit", subCommand));
    }
}
