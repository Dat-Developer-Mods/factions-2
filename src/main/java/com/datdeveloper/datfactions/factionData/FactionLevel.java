package com.datdeveloper.datfactions.factionData;

import com.datdeveloper.datfactions.database.DatabaseEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class FactionLevel extends DatabaseEntity {
    final ResourceKey<Level> id;

    /**
     * The default faction that owns all the chunks
     */
    UUID defaultOwner;

    // World Restrictions
    /**
     * Allow people to claim
     */
    boolean allowClaiming;
    boolean requireConnect;
    int maxLand;
    int landCost;
    int maxClaimRadius;

    // Stealing
    boolean allowLandSteal;
    boolean requireLandStealConnect;

    // Misc
    int teleportDelay;
    float passivePowerGainMultiplier;

    Map<ChunkPos, ChunkClaim> claims;

    public FactionLevel(ResourceKey<Level> id) {
        this.id = id;
        this.defaultOwner = FactionCollection.getInstance().SAFEZONE.getId();

        this.allowClaiming = true;
        this.requireConnect = true;
        this.maxLand = Integer.MAX_VALUE;
        this.landCost = 10;
        this.maxClaimRadius = 5;

        this.allowLandSteal = true;
        this.requireLandStealConnect = false;

        this.teleportDelay = 5;
        this.passivePowerGainMultiplier = 1.f;
    }

    public FactionLevel(ResourceKey<Level> id, FactionLevel template) {
        this.id = id;
        this.defaultOwner = template.defaultOwner;

        this.allowClaiming = template.allowClaiming;
        this.requireConnect = template.requireConnect;
        this.maxLand = template.maxLand;
        this.landCost = template.landCost;
        this.maxClaimRadius = template.maxClaimRadius;

        this.allowLandSteal = template.allowLandSteal;
        this.requireLandStealConnect = template.requireLandStealConnect;

        this.teleportDelay = template.teleportDelay;
        this.passivePowerGainMultiplier = template.passivePowerGainMultiplier;
    }

    public ResourceKey<Level> getId() {
        return id;
    }

    public UUID getDefaultOwner() {
        return defaultOwner;
    }

    public void setDefaultOwner(UUID defaultOwner) {
        this.defaultOwner = defaultOwner;
        markDirty();
    }

    public boolean isAllowClaiming() {
        return allowClaiming;
    }

    public void setAllowClaiming(boolean allowClaiming) {
        this.allowClaiming = allowClaiming;
        markDirty();
    }

    public boolean isRequireConnect() {
        return requireConnect;
    }

    public void setRequireConnect(boolean requireConnect) {
        this.requireConnect = requireConnect;
        markDirty();
    }

    public int getMaxLand() {
        return maxLand;
    }

    public void setMaxLand(int maxLand) {
        this.maxLand = maxLand;
        markDirty();
    }

    public int getLandCost() {
        return landCost;
    }

    public void setLandCost(int landCost) {
        this.landCost = landCost;
        markDirty();
    }

    public int getMaxClaimRadius() {
        return maxClaimRadius;
    }

    public void setMaxClaimRadius(int maxClaimRadius) {
        this.maxClaimRadius = maxClaimRadius;
        markDirty();
    }

    public boolean isAllowLandSteal() {
        return allowLandSteal;
    }

    public void setAllowLandSteal(boolean allowLandSteal) {
        this.allowLandSteal = allowLandSteal;
        markDirty();
    }

    public boolean isRequireLandStealConnect() {
        return requireLandStealConnect;
    }

    public void setRequireLandStealConnect(boolean requireLandStealConnect) {
        this.requireLandStealConnect = requireLandStealConnect;
        markDirty();
    }

    public int getTeleportDelay() {
        return teleportDelay;
    }

    public void setTeleportDelay(int teleportDelay) {
        this.teleportDelay = teleportDelay;
        markDirty();
    }

    public float getPassivePowerGainMultiplier() {
        return passivePowerGainMultiplier;
    }

    public void setPassivePowerGainMultiplier(float passivePowerGainMultiplier) {
        this.passivePowerGainMultiplier = passivePowerGainMultiplier;
        markDirty();
    }

    public Map<ChunkPos, ChunkClaim> getClaims() {
        return claims;
    }

    public int countClaims(@NotNull UUID factionId) {
        if (factionId.equals(defaultOwner)) return Integer.MAX_VALUE;
        return (int) claims.values().stream()
                .filter(claim -> claim.getFactionId() == factionId)
                .count();
    }

    public int getClaimsWorth(@NotNull UUID factionId) {
        if (factionId.equals(defaultOwner)) return 0;
        else return countClaims(factionId) * landCost;
    }

    public UUID getChunkOwner(ChunkPos pos) {
        ChunkClaim claim = claims.get(pos);
        return claim != null ? claim.getFactionId() : defaultOwner;
    }

    public void setChunkOwner(ChunkPos pos, Faction faction) {
        if (faction == null || faction.getId().equals(defaultOwner)) {
            claims.remove(pos);
            return;
        }

        claims.put(pos, new ChunkClaim(faction.getId()));
    }
}
