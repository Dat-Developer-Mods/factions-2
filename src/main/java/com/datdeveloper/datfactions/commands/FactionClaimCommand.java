package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factiondata.*;
import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
import com.datdeveloper.datfactions.util.ClaimUtil;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.*;

public class FactionClaimCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {

        final LiteralCommandNode<CommandSourceStack> claimCommand = Commands.literal("claim")
                .requires(commandSourceStack1 -> {
                    if (!(commandSourceStack1.isPlayer() && DatPermissions.hasAnyPermissions(commandSourceStack1.source, FactionPermissions.FACTION_CLAIM_ONE, FactionPermissions.FACTION_CLAIM_SQUARE, FactionPermissions.FACTION_CLAIM_AUTO)))
                        return false;
                    final FactionPlayer fPlayer1 = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack1.getPlayer());
                    return fPlayer1.hasFaction() && fPlayer1.getRole().hasAnyPermissions(ERolePermissions.CLAIMONE, ERolePermissions.CLAIMSQUARE, ERolePermissions.AUTOCLAIM);
                })
                .then(
                        Commands.literal("one")
                                .requires(commandSourceStack -> {
                                    final ServerPlayer player = commandSourceStack.getPlayer();
                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                    return DatPermissions.hasPermission(player, FactionPermissions.FACTION_CLAIM_ONE) && fPlayer.getRole().hasPermission(ERolePermissions.CLAIMONE);
                                })
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();

                                    return ClaimUtil.claimChunks(player, new ArrayList<>(List.of(new ChunkPos(player.getOnPos()))));
                                })

                )
                .then(
                        Commands.literal("square")
                                .requires(commandSourceStack -> {
                                    final ServerPlayer player = commandSourceStack.getPlayer();
                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                    return DatPermissions.hasPermission(player, FactionPermissions.FACTION_CLAIM_SQUARE) && fPlayer.getRole().hasPermission(ERolePermissions.CLAIMSQUARE);
                                })
                                .then(
                                        Commands.argument("radius", IntegerArgumentType.integer(1))
                                                .executes(c -> {
                                                    final ServerPlayer player = c.getSource().getPlayer();
                                                    final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
                                                    final FactionLevel level = FLevelCollection.getInstance().getByKey(player.level().dimension());

                                                    final int radius = c.getArgument("radius", int.class);

                                                    final int maxClaimRadius = level.getSettings().getMaxClaimRadius();
                                                    if (maxClaimRadius < radius) {
                                                        c.getSource().sendFailure(
                                                                Component.literal("You cannot claim a radius bigger than " + maxClaimRadius + " in ")
                                                                        .append(level.getNameWithDescription(fPlayer.getFaction()).withStyle(ChatFormatting.AQUA)));
                                                        return 2;
                                                    }

                                                    final ChunkPos playerChunk = new ChunkPos(player.getOnPos());

                                                    final List<ChunkPos> chunks = new ArrayList<>();
                                                    for (int i = -(radius - 1); i < radius; ++i) {
                                                        for (int j = -(radius - 1); j < radius; ++j) {
                                                            chunks.add(new ChunkPos(playerChunk.x + i, playerChunk.z + j));
                                                        }
                                                    }

                                                    return ClaimUtil.claimChunks(player, chunks);
                                                })
                                )
                )
                .then(
                        Commands.literal("auto")
                                .requires(commandSourceStack -> {
                                    final ServerPlayer player = commandSourceStack.getPlayer();
                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                    return DatPermissions.hasPermission(player, FactionPermissions.FACTION_CLAIM_AUTO) && fPlayer.getRole().hasPermission(ERolePermissions.AUTOCLAIM);
                                })
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);

                                    final boolean autoClaim = fPlayer.isAutoClaim();
                                    if (autoClaim) {
                                        fPlayer.setAutoClaim(false);
                                        player.sendSystemMessage(Component.literal(DatChatFormatting.TextColour.INFO + "Disabling auto-claiming, you will no longer claim chunks as you cross into their borders"));
                                    } else {
                                        fPlayer.setAutoClaim(true);
                                        player.sendSystemMessage(Component.literal(DatChatFormatting.TextColour.INFO + "Enabled auto-claiming, you will now claim chunks as you cross into their borders"));

                                        ClaimUtil.claimChunks(player, new ArrayList<>(List.of(new ChunkPos(player.getOnPos()))));
                                    }

                                    return 1;
                                })
                ).build();

        command.then(claimCommand);
    }
}
