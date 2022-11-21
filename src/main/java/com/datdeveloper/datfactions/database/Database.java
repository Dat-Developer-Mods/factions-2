package com.datdeveloper.datfactions.database;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionLevel;
import com.datdeveloper.datfactions.factionData.FactionLevelSettings;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class Database {
    public static Database instance;

    /**
     * Store an entity in the database
     * Only supports {@link Faction}, {@link FactionPlayer}, and {@link FactionLevel}
     * <br>
     * This bullshit was brought to you by trying to avoid some other object orientated bullshit.
     * @param entity a DatabaseEntity to store
     */
    public void storeEntity(DatabaseEntity entity) {
        if (entity instanceof Faction) storeFaction((Faction) entity);
        else if (entity instanceof FactionPlayer) storePlayer((FactionPlayer) entity);
        else if (entity instanceof FactionLevel) storeLevel((FactionLevel) entity);
        else throw new NotImplementedException();
    }

    /* ========================================= */
    /* Factions
    /* ========================================= */

    /**
     * Store a faction in the store
     * @param faction The faction to store
     */
    public abstract void storeFaction(Faction faction);

    /**
     * Delete a faction from the store
     * @param faction the faction to delete
     */
    public abstract void deleteFaction(Faction faction);

    /**
     * Load a faction by its ID
     * @param factionId The ID of the faction to load
     * @return the faction
     */
    @Nullable
    public abstract Faction loadFaction(UUID factionId);

    /**
     * Load the faction template
     * @return the faction template
     */
    @Nullable
    public abstract Faction loadFactionTemplate();

    /**
     * Store the faction template
     * @param template the faction template
     */
    public abstract void storeFactionTemplate(Faction template);

    /**
     * Get a list of all the factions in the store
     * @return A list of faction IDs in the store
     */
    public abstract List<UUID> getAllStoredFactions();

    /* ========================================= */
    /* Players
    /* ========================================= */

    /**
     * Store a player in the store
     * @param player The player to store
     */
    public abstract void storePlayer(FactionPlayer player);

    /**
     * Delete a player in the store
     * @param player The player to delete
     */
    public abstract void deletePlayer(FactionPlayer player);

    /**
     * Load a player from the store
     * @param playerId The ID of the player to store
     * @return the player
     */
    @Nullable
    public abstract FactionPlayer loadPlayer(UUID playerId);

    /**
     * Get a list of all the players in the store
     * @return A list of all the player IDs in the store
     */
    public abstract List<UUID> getAllStoredPlayers();

    /* ========================================= */
    /* Levels
    /* ========================================= */

    /**
     * Store a level in the store
     * @param level The level to store
     */
    public abstract void storeLevel(FactionLevel level);

    /**
     * Delete a level in the store
     * @param level The level to delete
     */
    public abstract void deleteLevel(FactionLevel level);

    /**
     * Load a level from the store
     * @param levelId The ID of the level to load
     * @return the level
     */
    @Nullable
    public abstract FactionLevel loadLevel(ResourceKey<Level> levelId);

    /**
     * Get a list of the levels in the store
     *
     * @return a list of all the level IDs in the store
     */
    public abstract List<ResourceKey<Level>> getAllStoredLevels();

    /**
     * Load the default level settings
     * @return the default settings
     */
    @Nullable
    public abstract FactionLevelSettings loadLevelDefaultSettings();

    /**
     * Store the default level settings
     * @param defaultSettings the default settings to store
     */
    public abstract void storeDefaultSettings(FactionLevelSettings defaultSettings);
}
