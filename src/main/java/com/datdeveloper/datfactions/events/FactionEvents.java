package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionLandChangeOwnerEvent;
import com.datdeveloper.datfactions.factionData.EFactionFlags;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.datdeveloper.datfactions.factionData.FactionLevel;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.function.Consumer;

/**
 * An EventBusSubscriber for subscribing to our own events
 */
@Mod.EventBusSubscriber(modid = Datfactions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FactionEvents {
    /**
     * Check chunk claim is legal
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void checkChunks(final FactionLandChangeOwnerEvent event) {
        final CommandSource source = event.getInstigator();

        final Consumer<Component> sendSourceMessage = (message -> {
            if (source != null) {
                source.sendSystemMessage(message);
            }
        });

        final Faction faction = event.getNewOwner();
        final FactionLevel level = event.getLevel();
        final List<ChunkPos> chunks = new ArrayList<>(event.getChunks());
        Set<ChunkPos> connectedChunks = new HashSet<>();
        final Map<Faction, Set<ChunkPos>> stolenChunks = new HashMap<>();

        if (event.isSkipDefaultChecks()) {
            connectedChunks = new HashSet<>(chunks);
            for (final ChunkPos chunk : connectedChunks) {
                final Faction owner = FactionCollection.getInstance().getByKey(level.getChunkOwner(chunk));
                stolenChunks.computeIfAbsent(owner, (key) -> new HashSet<>()).add(chunk);
            }
        } else {

            // Check Level
            if (!level.getSettings().isAllowClaiming()) {
                sendSourceMessage.accept(Component.literal(DatChatFormatting.TextColour.ERROR + "You cannot claim chunks in " + level.getNameWithDescription(faction)));
                event.setCanceled(true);
                return;
            }

            // Stealing
            for (final Iterator<ChunkPos> iterator = chunks.iterator(); iterator.hasNext(); ) {
                // ignore for now
                final ChunkPos chunk = iterator.next();
                final Faction owner = FactionCollection.getInstance().getByKey(level.getChunkOwner(chunk));
                if (!owner.hasFlag(EFactionFlags.DEFAULT) || owner.hasFlag(EFactionFlags.STRONGBORDERS))
                    iterator.remove();
            }
            // TODO: Stealing

            if (chunks.isEmpty()) {
                sendSourceMessage.accept(Component.literal(DatChatFormatting.TextColour.ERROR + "You cannot claim any chunks here"));
                event.setCanceled(true);
                return;
            }

            if (faction.getTotalLandCount() == 0) connectedChunks.add(chunks.remove(0));
            if (level.getSettings().isRequireConnect() && !faction.hasFlag(EFactionFlags.DEFAULT)) {
                int lastSize = -1;
                // Connected
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

                if (connectedChunks.isEmpty()) {
                    sendSourceMessage.accept(Component.literal(DatChatFormatting.TextColour.ERROR + "Chunks you claim must be connected to your existing territory in ")
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
                    sendSourceMessage.accept(Component.literal(DatChatFormatting.TextColour.ERROR + "You cannot claim that many more chunks in " + level.getNameWithDescription(faction)));
                    event.setCanceled(true);
                    return;
                }

                if ((faction.getTotalLandCount() + connectedChunks.size()) > FactionsConfig.getGlobalMaxFactionLandCount()) {
                    sendSourceMessage.accept(Component.literal(DatChatFormatting.TextColour.ERROR + "You cannot claim that many more chunks without running over the global limit"));
                    event.setCanceled(true);
                    return;
                }
            }

            if (!faction.hasFlag(EFactionFlags.INFINITEPOWER) && (faction.getTotalLandWorth() + (connectedChunks.size() * level.getSettings().getLandWorth())) > faction.getTotalPower()) {
                sendSourceMessage.accept(Component.literal(DatChatFormatting.TextColour.ERROR + "You don't have enough power to claim that much more land in " + level.getNameWithDescription(faction)));
                event.setCanceled(true);
                return;
            }
        }

        event.setChunks(connectedChunks);
    }
}
