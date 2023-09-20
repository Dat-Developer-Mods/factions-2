package com.datdeveloper.datfactions.util;

import com.datdeveloper.datfactions.api.events.FactionLandChangeOwnerEvent;
import com.datdeveloper.datfactions.factionData.*;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClaimUtil {
    public static int claimChunks(final ServerPlayer player, final List<ChunkPos> chunks) {
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
        Faction faction = fPlayer.getFaction();
        FactionLevel level = FLevelCollection.getInstance().getByKey(player.level().dimension());

        // Event
        final FactionLandChangeOwnerEvent event = new FactionLandChangeOwnerEvent(
                player,
                chunks,
                level,
                faction,
                FactionLandChangeOwnerEvent.EChangeOwnerReason.CLAIM
        );
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return -1;

        level = event.getLevel();
        faction = event.getNewOwner();

        final Map<Faction, Integer> stolen = new HashMap<>();
        for (final ChunkPos chunk : chunks) {
            final Faction owner = level.getChunkOwningFaction(chunk);
            if (!owner.hasFlag(EFactionFlags.WEAKBORDERS)) {
                stolen.put(faction, stolen.computeIfAbsent(faction, (key) -> 0) + 1);
            }

            level.setChunkOwner(chunk, faction);
        }

        if (level.getSettings().isNotifyLandOwnersOfSteal()) {
            for (final Faction owner : stolen.keySet()) {
                owner.sendFactionWideMessage(
                        faction.getNameWithDescription(owner)
                                .withStyle(RelationUtil.getRelation(owner, faction).formatting)
                                .append(DatChatFormatting.TextColour.ERROR + " has stolen ")
                                .append(stolen.get(owner) + " chunks from you")
                );
            }
        }

        player.sendSystemMessage(Component.literal(DatChatFormatting.TextColour.INFO + "Successfully claimed " + event.getChunks().size() + " chunks"));

        return event.getChunks().size();
    }
}
