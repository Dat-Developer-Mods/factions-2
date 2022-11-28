package com.datdeveloper.datfactions.factionData;

import com.datdeveloper.datfactions.database.DatabaseEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A representation of the level storing the claims in that level and level specific config
 */
public class FactionLevel extends DatabaseEntity {
    /**
     * The ID of the level
     */
    @NotNull
    final ResourceKey<Level> id;

    /**
     * The settings for the level
     */
    FactionLevelSettings settings;

    /**
     * The claims faction's have in the level
     */
    Map<ChunkPos, ChunkClaim> claims;

    // TODO: Add a faction or player blacklist to block a faction in the world

    public FactionLevel(final @NotNull ResourceKey<Level> id) {
        this.id = id;
        settings = null;
        claims = new HashMap<>();
    }

    public FactionLevel(final @NotNull ResourceKey<Level> id, final FactionLevelSettings settings) {
        this.id = id;
        this.settings = settings;
        claims = new HashMap<>();
    }

    /* ========================================= */
    /* Getters
    /* ========================================= */
    public @NotNull ResourceKey<Level> getId() {
        return id;
    }

    public Map<ChunkPos, ChunkClaim> getClaims() {
        return claims;
    }

    public ServerLevel getServerLevel() {
        return ServerLifecycleHooks.getCurrentServer().getLevel(getId());
    }

    public String getName() {
        return getId().location().getPath();
    }

    /* ========================================= */
    /* Claims
    /* ========================================= */
    public int getClaimsCount(@NotNull final UUID factionId) {
        if (factionId.equals(getSettings().defaultOwner)) return Integer.MAX_VALUE;
        return (int) claims.values().stream()
                .filter(claim -> claim.getFactionId().equals(factionId))
                .count();
    }

    public int getClaimsWorth(@NotNull final UUID factionId) {
        if (factionId.equals(getSettings().defaultOwner)) return 0;
        else return getClaimsCount(factionId) * getSettings().landWorth;
    }

    @NotNull
    public UUID getChunkOwner(final ChunkPos pos) {
        final ChunkClaim claim = claims.get(pos);
        return claim != null ? claim.getFactionId() : getSettings().defaultOwner;
    }

    public Faction getChunkOwningFaction(final ChunkPos pos) {
        return FactionCollection.getInstance().getByKey(getChunkOwner(pos));
    }

    public void setChunkOwner(final ChunkPos pos, final Faction faction) {
        if (faction == null || faction.getId().equals(getSettings().defaultOwner)) {
            claims.remove(pos);
            return;
        }

        claims.put(pos, new ChunkClaim(faction.getId()));
        markDirty();
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
        markDirty();
        return settings;
    }

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

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof FactionLevel fLevel) && this.getId().equals(fLevel.getId());
    }
}
