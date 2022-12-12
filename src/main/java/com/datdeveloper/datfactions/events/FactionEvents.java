package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionLandChangeOwnerEvent;
import com.datdeveloper.datfactions.factionData.EFactionFlags;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.datdeveloper.datfactions.factionData.FactionLevel;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * An EventBusSubscriber for subscribing to our own events
 */
@Mod.EventBusSubscriber(modid = Datfactions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FactionEvents {
    static void sendSourceMessage(final CommandSource source, final Component message) {
        if (source != null) {
            source.sendSystemMessage(message);
        }
    }

    /**
     * Check chunk claim is legal
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void checkChunks(final FactionLandChangeOwnerEvent event) {
        final CommandSource source = event.getInstigator();

        final Faction faction = event.getNewOwner();
        final FactionLevel level = event.getLevel();
        final List<ChunkPos> chunks = new ArrayList<>(event.getChunks());
        Set<ChunkPos> connectedChunks = new HashSet<>();
        final Map<Faction, Set<ChunkPos>> stolenChunks = new HashMap<>();

        if (event.isSkipDefaultChecks() || event.getReason() == FactionLandChangeOwnerEvent.EChangeOwnerReason.DISBAND) return;

        // Handle unclaim
        if (event.getReason() == FactionLandChangeOwnerEvent.EChangeOwnerReason.UNCLAIM) {
            // TODO: Disallow separating land clusters
            return;
        }

        // Check Level
        if (!level.getSettings().isAllowClaiming()) {
            sendSourceMessage(source,
                    Component.literal(DatChatFormatting.TextColour.ERROR + "You cannot claim chunks in ")
                            .append(level.getNameWithDescription(faction).withStyle(ChatFormatting.AQUA))
            );
            event.setCanceled(true);
            return;
        }

        // Stealing
        for (final Iterator<ChunkPos> iterator = chunks.iterator(); iterator.hasNext(); ) {
            final ChunkPos chunk = iterator.next();
            final Faction owner = FactionCollection.getInstance().getByKey(level.getChunkOwner(chunk));

            if (owner == null) continue;

            // Weak borders check happens first to avoid the cost of comparing power
            if (owner.hasFlag(EFactionFlags.WEAKBORDERS)) {
                continue;
            }

            if (!owner.hasFlag(EFactionFlags.STRONGBORDERS)) {
                stolenChunks.computeIfAbsent(owner, (key) -> new HashSet<>()).add(chunk);
                connectedChunks.add(chunk);
                iterator.remove();
            }
        }

        // Ensure the owning faction can afford to keep any of the land the faction is trying to steal, and check
        // if the faction's power is higher than the owner
        for (final Faction owner : stolenChunks.keySet()) {
            final int worthAfterStealing = (owner.getTotalLandWorth() - 1) * level.getSettings().getLandWorth();
            if (worthAfterStealing < owner.getTotalPower()
                    && owner.getTotalPower() > faction.getTotalPower()
            ) {
                sendSourceMessage(source, owner.getNameWithDescription(faction)
                        .withStyle(RelationUtil.getRelation(faction, owner).formatting)
                        .append(DatChatFormatting.TextColour.ERROR + " already owns that land and can afford to keep it"));
                event.setCanceled(true);
                return;
            }
        }

        if (faction.getLandCountInlevel(level) == 0 && !chunks.isEmpty()) connectedChunks.add(chunks.remove(0));

        // Check chunks are connected
        if (level.getSettings().isRequireConnect()) {
            int lastSize = -1;
            while (connectedChunks.size() != lastSize && chunks.size() > 0) {
                lastSize = connectedChunks.size();

                for (final Iterator<ChunkPos> iterator = chunks.iterator(); iterator.hasNext(); ) {
                    final ChunkPos chunk = iterator.next();
                    if (
                            level.doesChunkConnect(chunk, faction)
                                    || connectedChunks.contains(new ChunkPos(chunk.x + 1, chunk.z))
                                    || connectedChunks.contains(new ChunkPos(chunk.x, chunk.z + 1))
                                    || connectedChunks.contains(new ChunkPos(chunk.x - 1, chunk.z))
                                    || connectedChunks.contains(new ChunkPos(chunk.x, chunk.z - 1))
                    ) {
                        connectedChunks.add(chunk);
                        iterator.remove();
                    }
                }
            }

            if (!chunks.isEmpty()) {
                sendSourceMessage(source, Component.literal(DatChatFormatting.TextColour.ERROR + "Chunks you claim must be connected to your existing territory in ")
                        .append(level.getNameWithDescription(faction).withStyle(ChatFormatting.AQUA)));
                event.setCanceled(true);
                return;
            }
        } else {
            connectedChunks = new HashSet<>(chunks);
        }

        // Afford
        if (!faction.hasFlag(EFactionFlags.UNLIMITEDLAND)) {
            if ((level.getClaimsCount(faction.getId()) + connectedChunks.size()) > level.getSettings().getMaxLand()) {
                sendSourceMessage(source, Component.literal(DatChatFormatting.TextColour.ERROR + "You cannot claim that many more chunks in ")
                        .append(level.getNameWithDescription(faction).withStyle(ChatFormatting.AQUA)));
                event.setCanceled(true);
                return;
            }

            if ((faction.getTotalLandCount() + connectedChunks.size()) > FactionsConfig.getGlobalMaxFactionLandCount()) {
                sendSourceMessage(source, Component.literal(DatChatFormatting.TextColour.ERROR + "You cannot claim that many more chunks without running over the global limit"));
                event.setCanceled(true);
                return;
            }

            if (!faction.hasFlag(EFactionFlags.INFINITEPOWER) && (faction.getTotalLandWorth() + (connectedChunks.size() * level.getSettings().getLandWorth())) > faction.getMaxLandWorth()) {
                sendSourceMessage(source, Component.literal(DatChatFormatting.TextColour.ERROR + "You don't have enough power to claim that much more land in ")
                        .append(level.getNameWithDescription(faction).withStyle(ChatFormatting.AQUA)));
                event.setCanceled(true);
            }
        }
    }
}
