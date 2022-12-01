package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionPlayerChangeMembershipEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factionData.*;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONJOIN;

public class FactionJoinCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("join")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTIONJOIN))) return false;
                    final FactionPlayer fPlayer = getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return !fPlayer.hasFaction();
                })
                .then(Commands.argument("Target Faction", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            FactionCollection.getInstance().getAll().values().stream()
                                    .filter(faction -> faction.hasFlag(EFactionFlags.OPEN) || faction.invitedPlayer(context.getSource().getPlayer().getUUID()));
                            // Get factions
                            return builder.buildFuture();
                        })
                        .executes(c -> {
                            final String targetName = c.getArgument("Target Faction", String.class);
                            Faction target = FactionCollection.getInstance().getByName(targetName);
                            final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                            if (target == null) {
                                c.getSource().sendFailure(Component.literal("Cannot find a faction with that name"));
                                return 2;
                            } else if (!target.hasFlag(EFactionFlags.OPEN) && !target.invitedPlayer(fPlayer.getId())) {
                                c.getSource().sendFailure(Component.literal("You are not allowed to join that faction"));

                                target.sendFactionWideMessage(
                                        fPlayer.getNameWithDescription(target)
                                                .withStyle(RelationUtil.getRelation(target, fPlayer).formatting)
                                                .append(DatChatFormatting.TextColour.INFO + " attempted to join your faction but couldn't").append("\n")
                                                .append(DatChatFormatting.TextColour.INFO + "You can allow them to join by inviting them with ")
                                                .append(FactionCommandUtils.wrapCommand("/f invite " + fPlayer.getName()))
                                );

                                return 2;
                            }

                            final FactionPlayerChangeMembershipEvent event = new FactionPlayerChangeMembershipEvent(
                                    c.getSource().source,
                                    fPlayer,
                                    target,
                                    target.getRecruitRole(),
                                    FactionPlayerChangeMembershipEvent.EChangeFactionReason.JOIN
                            );
                            MinecraftForge.EVENT_BUS.post(event);
                            if (event.isCanceled()) return 0;

                            target = event.getNewFaction();
                            final FactionRole role = event.getNewRole();

                            fPlayer.setFaction(
                                    target != null ? target.getId() : null,
                                    role != null ? role.getId() : null,
                                    FactionPlayerChangeMembershipEvent.EChangeFactionReason.JOIN
                            );

                            if (target != null) target.removeInvite(fPlayer.getId());

                            return 1;
                        }))
                .build();

        command.then(subCommand);
    }
}
