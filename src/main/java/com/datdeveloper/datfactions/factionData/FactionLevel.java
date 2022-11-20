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

    public FactionLevel(ResourceKey<Level> id) {
        this.id = id;
        settings = new FactionLevelSettings();
    }

    public FactionLevel(ResourceKey<Level> id, FactionLevelSettings settings) {
        this.id = id;
        this.settings = settings;
    }

    public ResourceKey<Level> getId() {
        return id;
    }



    public Map<ChunkPos, ChunkClaim> getClaims() {
        return claims;
    }

    public int countClaims(@NotNull UUID factionId) {
        if (factionId.equals(settings.defaultOwner)) return Integer.MAX_VALUE;
        return (int) claims.values().stream()
                .filter(claim -> claim.getFactionId() == factionId)
                .count();
    }

    public int getClaimsWorth(@NotNull UUID factionId) {
        if (factionId.equals(settings.defaultOwner)) return 0;
        else return countClaims(factionId) * settings.landWorth;
    }

    public UUID getChunkOwner(ChunkPos pos) {
        ChunkClaim claim = claims.get(pos);
        return claim != null ? claim.getFactionId() : settings.defaultOwner;
    }

    public void setChunkOwner(ChunkPos pos, Faction faction) {
        if (faction == null || faction.getId().equals(settings.defaultOwner)) {
            claims.remove(pos);
            return;
        }

        claims.put(pos, new ChunkClaim(faction.getId()));
    }
}
