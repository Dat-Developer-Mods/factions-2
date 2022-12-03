package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionLandChangeOwnerEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factionData.*;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FactionUnclaimCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {

        final LiteralCommandNode<CommandSourceStack> claimCommand = Commands.literal("unclaim")
                .requires(commandSourceStack1 -> {
                    if (!(commandSourceStack1.isPlayer()) && DatPermissions.hasAnyPermissions(commandSourceStack1.source, FactionPermissions.FACTION_UNCLAIM_ONE, FactionPermissions.FACTION_UNCLAIM_SQUARE, FactionPermissions.FACTION_UNCLAIM_LEVEL, FactionPermissions.FACTION_UNCLAIM_ALL))
                        return false;
                    final FactionPlayer fPlayer1 = getPlayerOrTemplate(commandSourceStack1.getPlayer());
                    return fPlayer1.hasFaction() && fPlayer1.getRole().hasAnyPermissions(List.of(ERolePermissions.UNCLAIMONE, ERolePermissions.UNCLAIMSQUARE, ERolePermissions.UNCLAIMLEVEL, ERolePermissions.UNCLAIMALL));
                })
                .then(
                        Commands.literal("one")
                                .requires(commandSourceStack -> {
                                    final ServerPlayer player = commandSourceStack.getPlayer();
                                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                                    return DatPermissions.hasPermission(player, FactionPermissions.FACTION_UNCLAIM_ONE) && fPlayer.getRole().hasPermission(ERolePermissions.UNCLAIMONE);
                                })
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
                                    final FactionLevel level = FLevelCollection.getInstance().getByKey(player.getLevel().dimension());
                                    final ChunkPos chunk = new ChunkPos(player.getOnPos());

                                    if (!level.getChunkOwningFaction(chunk).equals(fPlayer.getFaction())) {
                                        c.getSource().sendFailure(Component.literal("You don't own that chunk"));
                                        return -2;
                                    }

                                    return unclaimChunks(c.getSource().getPlayer(), new ArrayList<>(List.of(new ChunkPos(player.getOnPos()))), level);
                                })

                )
                .then(
                        Commands.literal("square")
                                .requires(commandSourceStack -> {
                                    final ServerPlayer player = commandSourceStack.getPlayer();
                                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                                    return DatPermissions.hasPermission(player, FactionPermissions.FACTION_UNCLAIM_SQUARE) && fPlayer.getRole().hasPermission(ERolePermissions.UNCLAIMSQUARE);
                                })
                                .then(
                                        Commands.argument("radius", IntegerArgumentType.integer(1))
                                                .executes(c -> {
                                                    final ServerPlayer player = c.getSource().getPlayer();
                                                    final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
                                                    final FactionLevel level = FLevelCollection.getInstance().getByKey(player.getLevel().dimension());

                                                    final int radius = c.getArgument("radius", int.class);

                                                    final int maxClaimRadius = level.getSettings().getMaxClaimRadius();
                                                    if (maxClaimRadius < radius) {
                                                        c.getSource().sendFailure(
                                                                Component.literal("You cannot unclaim a radius bigger than " + maxClaimRadius + " in ")
                                                                        .append(level.getNameWithDescription(fPlayer.getFaction()).withStyle(ChatFormatting.AQUA))
                                                        );
                                                        return -2;
                                                    }

                                                    final ChunkPos playerChunk = new ChunkPos(player.getOnPos());

                                                    final List<ChunkPos> chunks = new ArrayList<>();
                                                    for (int i = -(radius - 1); i < radius; ++i) {
                                                        for (int j = -(radius - 1); j < radius; ++j) {
                                                            final ChunkPos chunk = new ChunkPos(playerChunk.x + i, playerChunk.z + j);
                                                            if (level.getChunkOwningFaction(chunk).equals(fPlayer.getFaction())) {
                                                                chunks.add(chunk);
                                                            }
                                                        }
                                                    }


                                                    if (chunks.isEmpty()) {
                                                        c.getSource().sendFailure(Component.literal("You don't any chunks in that radius"));
                                                        return -2;
                                                    }

                                                    return unclaimChunks(player, chunks, level);
                                                })
                                )
                )
                .then(
                        Commands.literal("level")
                                .requires(commandSourceStack -> {
                                    final ServerPlayer player = commandSourceStack.getPlayer();
                                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                                    return DatPermissions.hasPermission(player, FactionPermissions.FACTION_UNCLAIM_LEVEL) && fPlayer.getRole().hasPermission(ERolePermissions.UNCLAIMLEVEL);
                                })
                                .then(
                                        Commands.argument("areyousure", BoolArgumentType.bool())
                                                .executes(c -> {
                                                    final ServerPlayer player = c.getSource().getPlayer();
                                                    final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
                                                    final Faction faction = fPlayer.getFaction();
                                                    final FactionLevel level = FLevelCollection.getInstance().getByKey(player.getLevel().dimension());

                                                    final boolean areYouSure = BoolArgumentType.getBool(c, "areyousure");
                                                    if (!areYouSure) return 2;

                                                    final List<ChunkPos> chunks = level.getFactionChunks(fPlayer.getFaction());

                                                    if (chunks.isEmpty()) {
                                                        c.getSource().sendFailure(
                                                                faction.getNameWithDescription(faction)
                                                                        .withStyle(EFactionRelation.SELF.formatting)
                                                                        .append(DatChatFormatting.TextColour.ERROR + " does not own any chunks in ")
                                                                        .append(level.getNameWithDescription(fPlayer.getFaction()).withStyle(ChatFormatting.AQUA))
                                                        );
                                                        return -2;
                                                    }

                                                    return unclaimChunks(player, chunks, level);
                                                })
                                )
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                                    final Faction faction = fPlayer.getFaction();
                                    final FactionLevel level = FLevelCollection.getInstance().getByKey(player.getLevel().dimension());

                                    c.getSource().sendSuccess(Component.literal(
                                                            DatChatFormatting.TextColour.ERROR + "Are you sure you want to release all ")
                                                    .append(
                                                            faction.getNameWithDescription(faction)
                                                                    .withStyle(EFactionRelation.SELF.formatting)
                                                    )
                                                    .append(DatChatFormatting.TextColour.ERROR + " chunks in ")
                                                    .append(level.getNameWithDescription(fPlayer.getFaction()).withStyle(ChatFormatting.AQUA))
                                                    .append("? This action is not reversible\n")
                                                    .append(DatChatFormatting.TextColour.ERROR + "Use ")
                                                    .append(FactionCommandUtils.wrapCommand("/f unclaim level true", "/f unclaim level "))
                                                    .append(DatChatFormatting.TextColour.ERROR + " if you are")

                                            ,false);

                                    return 1;
                                })
                )
                .then(
                        Commands.literal("all")
                                .requires(commandSourceStack -> {
                                    final ServerPlayer player = commandSourceStack.getPlayer();
                                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                                    return DatPermissions.hasPermission(player, FactionPermissions.FACTION_UNCLAIM_ALL) && fPlayer.getRole().hasPermission(ERolePermissions.UNCLAIMALL);
                                })
                                .then(
                                        Commands.argument("areyousure", BoolArgumentType.bool())
                                                .executes(c -> {
                                                    final ServerPlayer player = c.getSource().getPlayer();
                                                    final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
                                                    final Faction faction = fPlayer.getFaction();

                                                    final boolean areYouSure = BoolArgumentType.getBool(c, "areyousure");
                                                    if (!areYouSure) return 2;

                                                    final Map<FactionLevel, List<ChunkPos>> chunks = FLevelCollection.getInstance().getAllFactionChunks(faction);
                                                    if (chunks.isEmpty()) {
                                                        c.getSource().sendFailure(
                                                                faction.getNameWithDescription(faction)
                                                                        .withStyle(EFactionRelation.SELF.formatting)
                                                                .append(DatChatFormatting.TextColour.ERROR + " does not own any chunks ")
                                                        );
                                                        return -2;
                                                    }

                                                    int count = 0;
                                                    for (final FactionLevel level : chunks.keySet()) {
                                                        count += unclaimChunks(player, chunks.get(level), level);
                                                    }

                                                    return count;
                                                })
                                )
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                                    final Faction faction = fPlayer.getFaction();
                                    final FactionLevel level = FLevelCollection.getInstance().getByKey(player.getLevel().dimension());

                                    c.getSource().sendSuccess(Component.literal(
                                                            DatChatFormatting.TextColour.ERROR + "Are you sure you want to release all ")
                                                    .append(
                                                            faction.getNameWithDescription(faction)
                                                                    .withStyle(EFactionRelation.SELF.formatting)
                                                    )
                                                    .append(DatChatFormatting.TextColour.ERROR + " chunks in? This action is not reversible\n")
                                                    .append(DatChatFormatting.TextColour.ERROR + "Use ")
                                                    .append(FactionCommandUtils.wrapCommand("/f unclaim all true", "/f unclaim all "))
                                                    .append(DatChatFormatting.TextColour.ERROR + " if you are")

                                            ,false);

                                    return 1;
                                })
                ).build();

        command.then(claimCommand);
    }

    public static int unclaimChunks(final ServerPlayer player, final List<ChunkPos> chunks, FactionLevel level) {
        final FactionPlayer fPlayer = getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        // Event
        final FactionLandChangeOwnerEvent event = new FactionLandChangeOwnerEvent(
                player,
                chunks,
                level,
                null,
                FactionLandChangeOwnerEvent.EChangeOwnerReason.UNCLAIM
        );
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return -1;

        level = event.getLevel();
        final Faction newOwner = event.getNewOwner();

        level.setChunksOwner(chunks, newOwner);

        player.sendSystemMessage(Component.literal(DatChatFormatting.TextColour.INFO + "Successfully unclaimed " + event.getChunks().size() + " chunks"));

        return event.getChunks().size();
    }
}
