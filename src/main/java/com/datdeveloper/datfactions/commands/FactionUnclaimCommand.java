package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionDisbandEvent;
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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public class FactionUnclaimCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {

        final LiteralCommandNode<CommandSourceStack> claimCommand = Commands.literal("unclaim")
                .requires(commandSourceStack1 -> {
                    if (!(commandSourceStack1.isPlayer()) && DatPermissions.hasAnyPermissions(commandSourceStack1.source, FactionPermissions.FACTIONUNCLAIMONE, FactionPermissions.FACTIONUNCLAIMSQUARE, FactionPermissions.FACTIONUNCLAIMALL))
                        return false;
                    final FactionPlayer fPlayer1 = getPlayerOrTemplate(commandSourceStack1.getPlayer());
                    return fPlayer1.hasFaction() && fPlayer1.getRole().hasAnyPermissions(List.of(ERolePermissions.UNCLAIMONE, ERolePermissions.UNCLAIMSQUARE, ERolePermissions.UNCLAIMALL));
                })
                .then(
                        Commands.literal("one")
                                .requires(commandSourceStack -> {
                                    final ServerPlayer player = commandSourceStack.getPlayer();
                                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                                    return DatPermissions.hasPermission(player, FactionPermissions.FACTIONUNCLAIMONE) && fPlayer.getRole().hasPermission(ERolePermissions.UNCLAIMONE);
                                })
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();

                                    return unclaimChunks(null, new ArrayList<>(List.of(new ChunkPos(player.getOnPos()))));
                                })

                )
                .then(
                        Commands.literal("square")
                                .requires(commandSourceStack -> {
                                    final ServerPlayer player = commandSourceStack.getPlayer();
                                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                                    return DatPermissions.hasPermission(player, FactionPermissions.FACTIONCLAIMSQUARE) && fPlayer.getRole().hasPermission(ERolePermissions.CLAIMSQUARE);
                                })
                                .then(
                                        Commands.argument("radius", IntegerArgumentType.integer(1))
                                                .executes(c -> {
                                                    final ServerPlayer player = c.getSource().getPlayer();
                                                    final FactionLevel level = FLevelCollection.getInstance().getByKey(player.getLevel().dimension());

                                                    final int radius = c.getArgument("radius", int.class);

                                                    final int maxClaimRadius = level.getSettings().getMaxClaimRadius();
                                                    if (maxClaimRadius < radius) {
                                                        c.getSource().sendFailure(Component.literal("You cannot claim a radius bigger than " + maxClaimRadius + " in " + level.getName()));
                                                        return 2;
                                                    }

                                                    final ChunkPos playerChunk = new ChunkPos(player.getOnPos());

                                                    final List<ChunkPos> chunks = new ArrayList<>();
                                                    for (int i = -(radius - 1); i < radius; ++i) {
                                                        for (int j = -(radius - 1); j < radius; ++j) {
                                                            chunks.add(new ChunkPos(playerChunk.x + i, playerChunk.z + j));
                                                        }
                                                    }

                                                    return unclaimChunks(player, chunks);
                                                })
                                )
                )
                .then(
                        Commands.literal("all")
                                .requires(commandSourceStack -> {
                                    final ServerPlayer player = commandSourceStack.getPlayer();
                                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                                    return DatPermissions.hasPermission(player, FactionPermissions.FACTIONCLAIMAUTO) && fPlayer.getRole().hasPermission(ERolePermissions.AUTOCLAIM);
                                })
                                .then(
                                        Commands.argument("areyousure", BoolArgumentType.bool())
                                                .executes(c -> {
                                                    final ServerPlayer player = c.getSource().getPlayer();
                                                    final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
                                                    final Faction faction = fPlayer.getFaction();

                                                    final boolean areYouSure = BoolArgumentType.getBool(c, "areyousure");
                                                    if (!areYouSure) return 2;

                                                    final FactionDisbandEvent event = new FactionDisbandEvent(c.getSource().source, faction);
                                                    MinecraftForge.EVENT_BUS.post(event);
                                                    if (event.isCanceled()) return 0;

                                                    FactionCollection.getInstance().disbandFaction(faction.getId());
                                                    return 1;
                                                })
                                )
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    FactionPlayer fPlayer = getPlayerOrTemplate(player);
                                    Faction faction = fPlayer.getFaction();

                                    c.getSource().sendSuccess(Component.literal(
                                                            DatChatFormatting.TextColour.ERROR + "Are you sure you want to release all ")
                                                    .append(
                                                            faction.getNameWithDescription(faction)
                                                                    .withStyle(EFactionRelation.SELF.formatting)
                                                    )
                                                    .append(DatChatFormatting.TextColour.ERROR + " chunks? This action is not reversible\n")
                                                    .append(DatChatFormatting.TextColour.ERROR + "Use ")
                                                    .append(FactionCommandUtils.wrapCommand("/f unclaim all true", "/f unclaim all "))
                                                    .append(DatChatFormatting.TextColour.ERROR + " if you are")

                                            ,false);

                                    return 1;
                                })
                ).build();

        command.then(claimCommand);
    }

    public static int unclaimChunks(final ServerPlayer player, final List<ChunkPos> chunks) {
        FactionLevel level = FLevelCollection.getInstance().getByKey(player.getLevel().dimension());

        // Event
        final FactionLandChangeOwnerEvent event = new FactionLandChangeOwnerEvent(player, chunks, level, null);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return -1;

        level = event.getLevel();
        Faction faction = event.getNewOwner();

        level.setChunksOwner(chunks, faction);

        player.sendSystemMessage(Component.literal(DatChatFormatting.TextColour.INFO + "Successfully unclaimed " + event.getChunks().size() + " chunks"));

        return event.getChunks().size();
    }
}
