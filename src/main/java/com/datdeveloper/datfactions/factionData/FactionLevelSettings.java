package com.datdeveloper.datfactions.factionData;

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

    // Misc
    /**
     * The delay before teleporting to this world with /factions home
     */
    int teleportDelay;

    /**
     * The passive power gain a player gets in this level
     */
    float passivePowerGainMultiplier;

    public FactionLevelSettings() {
        this.defaultOwner = FactionCollection.getInstance().WILDERNESS.getId();

        this.allowClaiming = true;
        this.requireConnect = true;
        this.maxLand = Integer.MAX_VALUE;
        this.landWorth = 10;
        this.maxClaimRadius = 5;

        this.allowLandSteal = true;
        this.requireLandStealConnect = false;

        this.teleportDelay = 5;
        this.passivePowerGainMultiplier = 1.f;
    }

    public FactionLevelSettings(FactionLevelSettings template) {
        this.defaultOwner = template.defaultOwner;

        this.allowClaiming = template.allowClaiming;
        this.requireConnect = template.requireConnect;
        this.maxLand = template.maxLand;
        this.landWorth = template.landWorth;
        this.maxClaimRadius = template.maxClaimRadius;

        this.allowLandSteal = template.allowLandSteal;
        this.requireLandStealConnect = template.requireLandStealConnect;

        this.teleportDelay = template.teleportDelay;
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

    public int getTeleportDelay() {
        return teleportDelay;
    }

    public float getPassivePowerGainMultiplier() {
        return passivePowerGainMultiplier;
    }

    /* ========================================= */
    /* Setters
    /* ========================================= */

    public void setDefaultOwner(UUID defaultOwner) {
        this.defaultOwner = defaultOwner;
        markDirty();
    }

    public void setAllowClaiming(boolean allowClaiming) {
        this.allowClaiming = allowClaiming;
        markDirty();
    }

    public void setRequireConnect(boolean requireConnect) {
        this.requireConnect = requireConnect;
        markDirty();
    }

    public void setMaxLand(int maxLand) {
        this.maxLand = maxLand;
        markDirty();
    }

    public void setLandWorth(int landWorth) {
        this.landWorth = landWorth;
        markDirty();
    }

    public void setMaxClaimRadius(int maxClaimRadius) {
        this.maxClaimRadius = maxClaimRadius;
        markDirty();
    }

    public void setAllowLandSteal(boolean allowLandSteal) {
        this.allowLandSteal = allowLandSteal;
        markDirty();
    }

    public void setRequireLandStealConnect(boolean requireLandStealConnect) {
        this.requireLandStealConnect = requireLandStealConnect;
        markDirty();
    }

    public void setTeleportDelay(int teleportDelay) {
        this.teleportDelay = teleportDelay;
        markDirty();
    }

    public void setPassivePowerGainMultiplier(float passivePowerGainMultiplier) {
        this.passivePowerGainMultiplier = passivePowerGainMultiplier;
        markDirty();
    }
}
