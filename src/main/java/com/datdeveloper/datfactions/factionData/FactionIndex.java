package com.datdeveloper.datfactions.factionData;

import java.util.*;

/**
 * A useful mapping of factions to players and players to factions
 */
public class FactionIndex {
    final Map<UUID, Faction> playerToFactionMap = new HashMap<>();
    final Map<UUID, Set<FactionPlayer>> factionToPlayerMap = new WeakHashMap<>();

    boolean initialised = false;


    private final static FactionIndex instance = new FactionIndex();
    public static FactionIndex getInstance() {
        return instance;
    }
    public boolean isInitialised() {
        return initialised;
    }

    /**
     * Get all the players in a faction
     * @param factionId The ID of the faction
     * @return A set of players in the faction
     */
    Set<FactionPlayer> getFactionPlayers(final UUID factionId) {
        return factionToPlayerMap.get(factionId);
    }

    /**
     * Update a player in the index
     * @param player the player to update
     */
    public void updatePlayer(final FactionPlayer player) {
        if (!initialised) return;
        final Faction previousFaction = playerToFactionMap.get(player.getId());

        if (previousFaction != null) factionToPlayerMap.get(previousFaction.getId()).remove(player);
        if (player.getFactionId() != null) factionToPlayerMap.get(player.getFactionId()).add(player);

        playerToFactionMap.put(player.getId(), player.getFactionId() != null ? FactionCollection.getInstance().getByKey(player.getFactionId()) : null);
    }

    /**
     * Delete a player from the index
     * @param playerId the playerId
     */
    public void deletePlayer(final UUID playerId) {
        final Faction faction = playerToFactionMap.remove(playerId);
        if (faction != null) {
            factionToPlayerMap.get(faction.getId()).removeIf(player -> player.getId().equals(playerId));
        }
    }

    /**
     * Add a new faction to the Index
     * @param faction The faction to add
     */
    public void addFaction(final Faction faction) {
        factionToPlayerMap.put(faction.getId(), new HashSet<>());
    }

    /**
     * Delete a faction from the index
     * @param factionId The ID of the faction
     */
    public void deleteFaction(final UUID factionId) {
        final Set<FactionPlayer> players = factionToPlayerMap.remove(factionId);

        for (final FactionPlayer player : players) {
            playerToFactionMap.put(player.getId(), null);
        }
    }

    /* ========================================= */
    /* Setup and teardown
    /* ========================================= */

    public void initialise() {
        // Add factions
        for (final UUID factionId : FactionCollection.getInstance().getAll().keySet()) {
            factionToPlayerMap.put(factionId, new HashSet<>());
        }

        // Add players
        for (final FactionPlayer player : FPlayerCollection.getInstance().getAll().values()) {
            playerToFactionMap.put(player.getId(), player.getFaction());
            if (player.hasFaction()) {
                factionToPlayerMap.get(player.getFactionId()).add(player);
            }
        }
        initialised = true;
    }

    public void uninitialise() {
        factionToPlayerMap.clear();
        playerToFactionMap.clear();
        initialised = false;
    }
}
