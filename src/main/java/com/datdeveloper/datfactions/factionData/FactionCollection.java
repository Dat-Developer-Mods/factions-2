package com.datdeveloper.datfactions.factionData;

import com.datdeveloper.datfactions.api.events.ChangeFactionMembershipEvent;
import com.datdeveloper.datfactions.database.Database;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FactionCollection extends BaseCollection<UUID, Faction>{

    Faction template;
    Faction WILDERNESS;

    static final FactionCollection instance = new FactionCollection();
    public static FactionCollection getInstance() {
        return instance;
    }

    public Faction createFaction(final String name) {
        final UUID factionId = UUID.randomUUID();
        final Faction newFaction = new Faction(factionId, name, template);
        map.put(factionId, newFaction);
        Database.instance.storeFaction(newFaction);
        FactionIndex.getInstance().addFaction(newFaction);

        return newFaction;
    }

    public void disbandFaction(final UUID factionId) {
        final Faction faction = map.remove(factionId);
        final Set<FactionPlayer> players = faction.getPlayers();
        FactionIndex.getInstance().deleteFaction(factionId);

        // Remove from relations
        for (final Faction otherFaction : map.values()) {
            otherFaction.setRelation(otherFaction, EFactionRelation.NEUTRAL);
        }

        // Remove from players
        for (final FactionPlayer player : players) {
            final ChangeFactionMembershipEvent event = new ChangeFactionMembershipEvent(null, player, null, null, ChangeFactionMembershipEvent.EChangeFactionReason.DISBAND);
            MinecraftForge.EVENT_BUS.post(event);

            final Faction newFaction = event.getNewFaction();
            final FactionRole newRole = event.getNewRole();
            player.setFaction(newFaction != null ? newFaction.getId() : null, newRole != null ? newRole.getId() : null);
        }
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
        final UUID WildernessId = new UUID(0, 1);
        WILDERNESS = getByKey(WildernessId);
        if (WILDERNESS == null) {
            WILDERNESS = new Faction(WildernessId, "Wilderness");
            WILDERNESS.description = "Uncharted Territory";
            WILDERNESS.creationTime = 0;
            WILDERNESS.addFlag(EFactionFlags.PERMANENT);
            WILDERNESS.addFlag(EFactionFlags.DEFAULT);
            WILDERNESS.addFlag(EFactionFlags.INFINITEPOWER);
            WILDERNESS.addFlag(EFactionFlags.FRIENDLYFIRE);
            WILDERNESS.addFlag(EFactionFlags.UNCHARTED);
            WILDERNESS.addFlag(EFactionFlags.UNLIMITEDLAND);
            WILDERNESS.addFlag(EFactionFlags.UNRELATEABLE);
            WILDERNESS.addFlag(EFactionFlags.ANONYMOUS);
            WILDERNESS.addFlag(EFactionFlags.OPENBUILD);
            Database.instance.storeFaction(WILDERNESS);
            map.put(WildernessId, WILDERNESS);
        }
    }

    public Faction getByName(final String name) {
        return map.values().stream()
                .filter(faction -> faction.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
