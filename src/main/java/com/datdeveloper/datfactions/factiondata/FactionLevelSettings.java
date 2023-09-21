package com.datdeveloper.datfactions.factiondata;

import com.datdeveloper.datfactions.database.DatabaseEntity;

import java.util.UUID;

/**
 * A store of a levels settings
 */
public class FactionLevelSettings extends DatabaseEntity {
    /**
     * The default faction that owns all the chunks
     */
    UUID defaultOwner;

    // World Restrictions
    /**
     * Whether to allow players to claim in this world
     */
    boolean allowClaiming;

    /**
     * Whether land must connect when claiming in this world
     */
    boolean requireConnect;

    /**
     * Whether the faction home must be on an owned chunk
     */
    boolean homeRequiresOwnedChunk;

    // TODO: Min players to claim

    /**
     * The maximum amount of land a faction can have in this world
     */
    int maxLand;

    /**
     * The worth of land in this world
     * (How much faction power it consumes to claim it)
     */
    int landWorth;

    /**
     * The maximum claim radius a player can use with /factions claim square
     */
    int maxClaimRadius;

    // Stealing
    /**
     * Whether factions can steal land in this world
     */
    boolean allowLandSteal;
    /**
     * Whether stolen land needs to connect to the faction's existing land
     */
    boolean requireLandStealConnect;

    /**
     * Whether to inform chunk owners that their chunk has been stolen
     */
    boolean notifyLandOwnersOfSteal;

    // Misc
    /**
     * The passive power gain a player gets in this level
     */
    float passivePowerGainMultiplier;

    public FactionLevelSettings() {
        this.defaultOwner = FactionCollection.getInstance().WILDERNESS.getId();

        this.allowClaiming = true;
        this.requireConnect = true;
        this.homeRequiresOwnedChunk = true;
        this.maxLand = Integer.MAX_VALUE;
        this.landWorth = 10;
        this.maxClaimRadius = 3;

        this.allowLandSteal = true;
        this.requireLandStealConnect = false;
        this.notifyLandOwnersOfSteal = false;

        this.passivePowerGainMultiplier = 1.f;
    }

    public FactionLevelSettings(final FactionLevelSettings template) {
        this.defaultOwner = template.defaultOwner;

        this.allowClaiming = template.allowClaiming;
        this.requireConnect = template.requireConnect;
        this.homeRequiresOwnedChunk = template.homeRequiresOwnedChunk;
        this.maxLand = template.maxLand;
        this.landWorth = template.landWorth;
        this.maxClaimRadius = template.maxClaimRadius;

        this.allowLandSteal = template.allowLandSteal;
        this.requireLandStealConnect = template.requireLandStealConnect;
        this.notifyLandOwnersOfSteal = template.notifyLandOwnersOfSteal;

        this.passivePowerGainMultiplier = template.passivePowerGainMultiplier;
    }

    /* ========================================= */
    /* Getters
    /* ========================================= */

    public UUID getDefaultOwner() {
        return defaultOwner;
    }

    public boolean isAllowClaiming() {
        return allowClaiming;
    }

    public boolean isRequireConnect() {
        return requireConnect;
    }

    public boolean isHomeRequiresOwnedChunk() {
        return homeRequiresOwnedChunk;
    }

    public boolean isNotifyLandOwnersOfSteal() {
        return notifyLandOwnersOfSteal;
    }

    public int getMaxLand() {
        return maxLand;
    }

    public int getLandWorth() {
        return landWorth;
    }

    public int getMaxClaimRadius() {
        return maxClaimRadius;
    }

    public boolean isAllowLandSteal() {
        return allowLandSteal;
    }

    public boolean isRequireLandStealConnect() {
        return requireLandStealConnect;
    }

    public float getPassivePowerGainMultiplier() {
        return passivePowerGainMultiplier;
    }

    /* ========================================= */
    /* Setters
    /* ========================================= */

    public void setDefaultOwner(final UUID defaultOwner) {
        this.defaultOwner = defaultOwner;
        markDirty();
    }

    public void setAllowClaiming(final boolean allowClaiming) {
        this.allowClaiming = allowClaiming;
        markDirty();
    }

    public void setRequireConnect(final boolean requireConnect) {
        this.requireConnect = requireConnect;
        markDirty();
    }

    public void setHomeRequiresOwnedChunk(final boolean homeRequiresOwnedChunk) {
        this.homeRequiresOwnedChunk = homeRequiresOwnedChunk;
        markDirty();
    }

    public void setNotifyLandOwnersOfSteal(final boolean notifyLandOwnersOfSteal) {
        this.notifyLandOwnersOfSteal = notifyLandOwnersOfSteal;
        markDirty();
    }

    public void setMaxLand(final int maxLand) {
        this.maxLand = maxLand;
        markDirty();
    }

    public void setLandWorth(final int landWorth) {
        this.landWorth = landWorth;
        markDirty();
    }

    public void setMaxClaimRadius(final int maxClaimRadius) {
        this.maxClaimRadius = maxClaimRadius;
        markDirty();
    }

    public void setAllowLandSteal(final boolean allowLandSteal) {
        this.allowLandSteal = allowLandSteal;
        markDirty();
    }

    public void setRequireLandStealConnect(final boolean requireLandStealConnect) {
        this.requireLandStealConnect = requireLandStealConnect;
        markDirty();
    }

    public void setPassivePowerGainMultiplier(final float passivePowerGainMultiplier) {
        this.passivePowerGainMultiplier = passivePowerGainMultiplier;
        markDirty();
    }
}
