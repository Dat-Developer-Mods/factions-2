package com.datdeveloper.datfactions.factionData;

import java.util.*;

/**
 * A useful mapping of factions to players and players to factions
 */
public class FactionIndex {
    Map<UUID, Faction> playerToFactionMap = new HashMap<>();
    Map<UUID, Set<FactionPlayer>> factionToPlayerMap = new WeakHashMap<>();

    boolean initialised = false;


    private final static FactionIndex instance = new FactionIndex();
    public static FactionIndex getInstance() {
        return instance;
    }

    /**
     * Get all the players in a faction
     * @param factionId The ID of the faction
     * @return A set of players in the faction
     */
    Set<FactionPlayer> getFactionPlayers(UUID factionId) {
        return factionToPlayerMap.get(factionId);
    }

    /**
     * Update a player in the index
     * @param player the player to update
     */
    public void updatePlayer(FactionPlayer player) {
        if (!initialised) return;
        Faction previousFaction = playerToFactionMap.get(player.getId());

        if (previousFaction != null) factionToPlayerMap.get(previousFaction.getId()).remove(player);
        if (player.getFactionId() != null) factionToPlayerMap.get(player.getFactionId()).add(player);

        playerToFactionMap.put(player.getId(), player.getFactionId() != null ? FactionCollection.getInstance().getByKey(player.getFactionId()) : null);
    }

    /**
     * Delete a player from the index
     * @param playerId the playerId
     */
    public void deletePlayer(UUID playerId) {
        Faction faction = playerToFactionMap.remove(playerId);
        if (faction != null) {
            factionToPlayerMap.get(faction.getId()).removeIf(player -> player.getId().equals(playerId));
        }
    }

    /**
     * Add a new faction to the Index
     * @param faction The faction to add
     */
    public void addFaction(Faction faction) {
        factionToPlayerMap.put(faction.getId(), new HashSet<>());
    }

    /**
     * Delete a faction from the index
     * @param factionId The ID of the faction
     */
    public void deleteFaction(UUID factionId) {
        Set<FactionPlayer> players = factionToPlayerMap.remove(factionId);

        for (FactionPlayer player : players) {
            playerToFactionMap.put(player.getId(), null);
        }
    }

    public void iniitialise() {
    }
}
