package com.datdeveloper.datfactions.factiondata;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionCreateEvent;
import com.datdeveloper.datfactions.api.events.FactionDisbandEvent;
import com.datdeveloper.datfactions.api.events.FactionPlayerChangeMembershipEvent;
import com.datdeveloper.datfactions.api.events.FactionLandChangeOwnerEvent;
import com.datdeveloper.datfactions.database.Database;
import com.datdeveloper.datfactions.exceptions.FactionNameTakenException;
import com.datdeveloper.datfactions.exceptions.StringTooLongException;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import com.datdeveloper.datfactions.factiondata.relations.EFactionRelation;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FactionCollection extends BaseCollection<UUID, Faction> {
    /**
     * The template new factions are based off of
     */
    Faction template;

    /**
     * The default faction
     */
    Faction WILDERNESS;

    static final FactionCollection instance = new FactionCollection();
    public static FactionCollection getInstance() {
        return instance;
    }

    /* ========================================= */
    /* Getters
    /* ========================================= */

    public Faction getTemplate() {
        return template;
    }

    /**
     * Check if a faction exists with the given name
     * @param name The name to check for
     * @return true if the name is taken
     */
    public boolean isNameTaken(final String name) {
        return map.values().stream()
                .anyMatch(faction -> faction.getName().equals(name));
    }

    /* ========================================= */
    /* Creation and Deletion
    /* ========================================= */

    public Faction createFaction(final String name) {
        if (name.length() > FactionsConfig.getMaxFactionNameLength()) {
            throw new StringTooLongException(FactionsConfig.getMaxFactionNameLength(), name, "That name is too long");
        } else if (FactionCollection.getInstance().isNameTaken(name)) {
            throw new FactionNameTakenException("That name has already been taken");
        }

        final UUID factionId = UUID.randomUUID();
        final Faction newFaction = new Faction(factionId, name, template);
        map.put(factionId, newFaction);
        Database.instance.storeFaction(newFaction);
        FactionIndex.getInstance().addFaction(newFaction);

        MinecraftForge.EVENT_BUS.post(new FactionCreateEvent.Post(newFaction));

        return newFaction;
    }

    public void disbandFaction(final UUID factionId) {
        final Faction faction = map.remove(factionId);
        Database.instance.deleteFaction(faction);

        MinecraftForge.EVENT_BUS.post(new FactionDisbandEvent.Post(faction));

        faction.sendFactionWideMessage(
                Component.literal(DatChatFormatting.TextColour.HEADER + faction.getName())
                        .append(DatChatFormatting.TextColour.INFO + " has been disbanded")
        );

        final Set<FactionPlayer> players = faction.getPlayers();

        // Remove from index
        FactionIndex.getInstance().deleteFaction(faction);

        // Remove from relations
        for (final Faction otherFaction : map.values()) {
            otherFaction.setRelation(faction, EFactionRelation.NEUTRAL);
        }

        // Release all land
        final Map<FactionLevel, List<ChunkPos>> allFactionChunks = FLevelCollection.getInstance().getAllFactionChunks(faction);
        for (final FactionLevel factionLevel : allFactionChunks.keySet()) {
            final List<ChunkPos> chunks = allFactionChunks.get(factionLevel);

            // TODO: Fix event shit
            final FactionLandChangeOwnerEvent.Pre event = new FactionLandChangeOwnerEvent.Pre(null, chunks, factionLevel, null, FactionLandChangeOwnerEvent.EChangeOwnerReason.DISBAND);
            MinecraftForge.EVENT_BUS.post(event);

            factionLevel.setChunksOwner(chunks, event.getNewOwner());
        }

        // Remove from players
        for (final FactionPlayer player : players) {
            final FactionPlayerChangeMembershipEvent.Pre event = new FactionPlayerChangeMembershipEvent.Pre(null, player, null, null, FactionPlayerChangeMembershipEvent.EChangeFactionReason.DISBAND);
            MinecraftForge.EVENT_BUS.post(event);

            final Faction newFaction = event.getNewFaction();
            final FactionRole newRole = event.getNewRole();
            player.setFaction(newFaction, newRole, FactionPlayerChangeMembershipEvent.EChangeFactionReason.DISBAND);
        }
    }

    /* ========================================= */
    /* Setup and teardown
    /* ========================================= */

    @Override
    public void initialise() {
        // Load All Factions
        final List<UUID> storedFactions = Database.instance.getAllStoredFactions();
        for (final UUID factionId : storedFactions) {
            final Faction faction = Database.instance.loadFaction(factionId);
            if (faction != null) {
                map.put(factionId, faction);
            }
        }

        // Load Template faction
        template = Database.instance.loadFactionTemplate();
        if (template == null) {
            template = new Faction(null, null);
            Database.instance.storeFactionTemplate(template);
        }

        // Create wilderness if it doesn't exist
        final UUID wildernessId = new UUID(0, 1);
        WILDERNESS = getByKey(wildernessId);
        if (WILDERNESS == null) {
            WILDERNESS = new Faction(wildernessId, "Wilderness");
            WILDERNESS.description = "Uncharted Territory";
            WILDERNESS.creationTime = 0;
            WILDERNESS.addFlag(EFactionFlags.DEFAULT);
            WILDERNESS.addFlag(EFactionFlags.PERMANENT);
            WILDERNESS.addFlag(EFactionFlags.FRIENDLYFIRE);
            WILDERNESS.addFlag(EFactionFlags.UNCHARTED);
            WILDERNESS.addFlag(EFactionFlags.INFINITEPOWER);
            WILDERNESS.addFlag(EFactionFlags.UNLIMITEDLAND);
            WILDERNESS.addFlag(EFactionFlags.UNRELATEABLE);
            WILDERNESS.addFlag(EFactionFlags.ANONYMOUS);
            WILDERNESS.addFlag(EFactionFlags.OPENBUILD);
            WILDERNESS.addFlag(EFactionFlags.WEAKBORDERS);
            Database.instance.storeFaction(WILDERNESS);
            map.put(wildernessId, WILDERNESS);
        }
    }

    public Faction getByName(final String name) {
        return map.values().stream()
                .filter(faction -> faction.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
