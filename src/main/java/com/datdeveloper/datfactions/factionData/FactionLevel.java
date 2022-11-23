package com.datdeveloper.datfactions.factionData;

import com.datdeveloper.datfactions.database.DatabaseEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * A representation of the level storing the claims in that level and level specific config
 */
public class FactionLevel extends DatabaseEntity {
    /**
     * The ID of the level
     */
    final ResourceKey<Level> id;

    /**
     * The settings for the level
     */
    FactionLevelSettings settings;

    /**
     * The claims faction's have in the level
     */
    Map<ChunkPos, ChunkClaim> claims;

    public FactionLevel(final ResourceKey<Level> id) {
        this.id = id;
        settings = new FactionLevelSettings();
    }

    public FactionLevel(final ResourceKey<Level> id, final FactionLevelSettings settings) {
        this.id = id;
        this.settings = settings;
    }

    /* ========================================= */
    /* Getters
    /* ========================================= */
    public ResourceKey<Level> getId() {
        return id;
    }

    public Map<ChunkPos, ChunkClaim> getClaims() {
        return claims;
    }


    /* ========================================= */
    /* Claims
    /* ========================================= */
    public int countClaims(@NotNull final UUID factionId) {
        if (factionId.equals(settings.defaultOwner)) return Integer.MAX_VALUE;
        return (int) claims.values().stream()
                .filter(claim -> claim.getFactionId() == factionId)
                .count();
    }

    public int getClaimsWorth(@NotNull final UUID factionId) {
        if (factionId.equals(settings.defaultOwner)) return 0;
        else return countClaims(factionId) * settings.landWorth;
    }

    public UUID getChunkOwner(final ChunkPos pos) {
        final ChunkClaim claim = claims.get(pos);
        return claim != null ? claim.getFactionId() : settings.defaultOwner;
    }

    public void setChunkOwner(final ChunkPos pos, final Faction faction) {
        if (faction == null || faction.getId().equals(settings.defaultOwner)) {
            claims.remove(pos);
            return;
        }

        claims.put(pos, new ChunkClaim(faction.getId()));
    }

    /* ========================================= */
    /* Settings
    /* ========================================= */

    /**
     * Get the settings object used by the level
     * If the level doesn't have its own settings, then this will return the default settings
     * Do not modify this object
     * @see #getSettingsToChange()
     * @return The level settings
     */
    public FactionLevelSettings getSettings() {
        return settings != null ? settings : FLevelCollection.getInstance().defaultSettings;
    }

    /**
     * Gets the level specific settings, creating them if they don't yet exist
     * @see #getSettings()
     * @return the level specific changes
     */
    public FactionLevelSettings getSettingsToChange() {
        if (settings == null) settings = new FactionLevelSettings(FLevelCollection.getInstance().defaultSettings);

        return settings;
    }
    
    /* ========================================= */
    /* Database Stuff
    /* ========================================= */

    @Override
    public void markClean() {
        super.markClean();
        if (settings != null) settings.markClean();
    }

    @Override
    public boolean isDirty() {
        return super.isDirty() || (settings != null && settings.isDirty());
    }
}
