package com.datdeveloper.datfactions.factionData;

import java.util.UUID;

/**
 * A store of a levels settings
 */
public class FactionLevelSettings {
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
        this.defaultOwner = FactionCollection.getInstance().SAFEZONE.getId();

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

    public UUID getDefaultOwner() {
        return defaultOwner;
    }

    public void setDefaultOwner(UUID defaultOwner) {
        this.defaultOwner = defaultOwner;
    }

    public boolean isAllowClaiming() {
        return allowClaiming;
    }

    public void setAllowClaiming(boolean allowClaiming) {
        this.allowClaiming = allowClaiming;
    }

    public boolean isRequireConnect() {
        return requireConnect;
    }

    public void setRequireConnect(boolean requireConnect) {
        this.requireConnect = requireConnect;
    }

    public int getMaxLand() {
        return maxLand;
    }

    public void setMaxLand(int maxLand) {
        this.maxLand = maxLand;
    }

    public int getLandWorth() {
        return landWorth;
    }

    public void setLandWorth(int landWorth) {
        this.landWorth = landWorth;
    }

    public int getMaxClaimRadius() {
        return maxClaimRadius;
    }

    public void setMaxClaimRadius(int maxClaimRadius) {
        this.maxClaimRadius = maxClaimRadius;
    }

    public boolean isAllowLandSteal() {
        return allowLandSteal;
    }

    public void setAllowLandSteal(boolean allowLandSteal) {
        this.allowLandSteal = allowLandSteal;
    }

    public boolean isRequireLandStealConnect() {
        return requireLandStealConnect;
    }

    public void setRequireLandStealConnect(boolean requireLandStealConnect) {
        this.requireLandStealConnect = requireLandStealConnect;
    }

    public int getTeleportDelay() {
        return teleportDelay;
    }

    public void setTeleportDelay(int teleportDelay) {
        this.teleportDelay = teleportDelay;
    }

    public float getPassivePowerGainMultiplier() {
        return passivePowerGainMultiplier;
    }

    public void setPassivePowerGainMultiplier(float passivePowerGainMultiplier) {
        this.passivePowerGainMultiplier = passivePowerGainMultiplier;
    }
}
