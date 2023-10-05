package com.datdeveloper.datfactions.factiondata;

import com.datdeveloper.datfactions.exceptions.FactionIndexNotLoadedException;

import java.util.*;

/**
 * A useful mapping of factions to players and players to factions
 */
public class FactionIndex {
    final Map<FactionPlayer, Faction> playerToFactionMap = new HashMap<>();
    final Map<Faction, Set<FactionPlayer>> factionToPlayerMap = new HashMap<>();

    boolean initialised = false;

    private static final FactionIndex instance = new FactionIndex();
    public static FactionIndex getInstance() {
        return instance;
    }
    public boolean isInitialised() {
        return initialised;
    }

    /* ========================================= */
    /* Players
    /* ========================================= */

    /**
     * Get all the players in a faction
     * @param faction The faction
     * @return A set of players in the faction
     */
    Set<FactionPlayer> getFactionPlayers(final Faction faction) {
        if (!initialised) throw new FactionIndexNotLoadedException();
        final Set<FactionPlayer> players = factionToPlayerMap.get(faction);
        return players != null ? Collections.unmodifiableSet(players) : Collections.emptySet();
    }

    /**
     * Update a player in the index
     * @param player the player to update
     */
    public void updatePlayerFaction(final FactionPlayer player) {
        if (!initialised) throw new FactionIndexNotLoadedException();
        final Faction previousFaction = playerToFactionMap.get(player);

        if (previousFaction != null) factionToPlayerMap.get(previousFaction).remove(player);
        if (player.getFactionId() != null) factionToPlayerMap.get(player.getFaction()).add(player);

        playerToFactionMap.put(player, player.hasFaction() ? FactionCollection.getInstance().getByKey(player.getFactionId()) : null);
    }

    /**
     * Delete a player from the index
     * @param player the player
     */
    public void deletePlayer(final FactionPlayer player) {
        if (!initialised) throw new FactionIndexNotLoadedException();
        final Faction faction = playerToFactionMap.remove(player);
        if (faction != null) {
            factionToPlayerMap.get(faction).removeIf(playerEl -> playerEl.equals(player));
        }
    }

    /* ========================================= */
    /* Factions
    /* ========================================= */

    /**
     * Add a new faction to the Index
     * @param faction The faction to add
     */
    public void addFaction(final Faction faction) {
        if (!initialised) throw new FactionIndexNotLoadedException();
        factionToPlayerMap.put(faction, new HashSet<>());
    }

    /**
     * Delete a faction from the index
     * @param faction The ID of the faction
     */
    public void deleteFaction(final Faction faction) {
        if (!initialised) throw new FactionIndexNotLoadedException();
        final Set<FactionPlayer> players = factionToPlayerMap.remove(faction);

        for (final FactionPlayer player : players) {
            playerToFactionMap.put(player, null);
        }
    }

    /* ========================================= */
    /* Setup and teardown
    /* ========================================= */

    public void initialise() {
        // Add factions
        for (final Faction faction : FactionCollection.getInstance().getAll().values()) {
            factionToPlayerMap.put(faction, new HashSet<>());
        }

        // Add players
        for (final FactionPlayer player : FPlayerCollection.getInstance().getAll().values()) {
            final Faction faction = player.getFaction();
            playerToFactionMap.put(player, faction);
            if (faction != null) {
                factionToPlayerMap.get(faction).add(player);
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
