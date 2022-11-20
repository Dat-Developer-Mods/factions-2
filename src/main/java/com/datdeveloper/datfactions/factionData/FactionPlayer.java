package com.datdeveloper.datfactions.factionData;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.database.DatabaseEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The representation of a player
 */
public class FactionPlayer extends DatabaseEntity {
    /**
     * The id of the player
     */
    UUID id;

    /**
     * The name of the player the last time they logged in
     */
    String lastName;

    /**
     * The last time the player was active on the server
     */
    long lastActiveTime;

    /**
     * The amount of power the player has
     */
    int power;

    /**
     * The maximum amount of power the player can have
     */
    int maxPower;

    /**
     * The ID of the faction the player belongs to
     */
    UUID factionId;

    /**
     * The ID of the role of the player in its faction
     */
    UUID role;

    /**
     * Whether the player is currently autoclaiming
     */
    transient boolean autoClaim = false;

    /**
     * The chat mode the player is using
     */
    transient EFPlayerChatMode chatMode = EFPlayerChatMode.PUBLIC;

    public FactionPlayer(UUID id, String lastName) {
        this.id = id;
        this.lastName = lastName;
        this.lastActiveTime = System.currentTimeMillis();

        this.maxPower = FactionsConfig.getPlayerStartingPower();
        this.power = this.maxPower;
        this.factionId = null;
        this.role = null;
    }

    public FactionPlayer(ServerPlayer player, FactionPlayer template) {
        this.id = player.getUUID();
        this.lastName = player.getDisplayName().getString();
        this.lastActiveTime = System.currentTimeMillis();
        this.power = template.power;
        this.maxPower = template.maxPower;
        this.factionId = template.factionId;
        this.role = template.role;
    }

    /* ========================================= */
    /* Getters
    /* ========================================= */

    public UUID getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public int getPower() {
        return power;
    }

    public int getMaxPower() {
        return maxPower;
    }

    public boolean hasFaction() {
        return factionId != null;
    }

    public UUID getFactionId() {
        return factionId;
    }

    public Faction getFaction() {
        if (!hasFaction()) return null;
        return FactionCollection.getInstance().getByKey(factionId);
    }

    public UUID getRole() {
        return role;
    }

    public boolean isAutoClaim() {
        return autoClaim;
    }

    /* ========================================= */
    /* Setters
    /* ========================================= */

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.markDirty();
    }

    public void setLastActiveTime(long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
        this.markDirty();
    }

    public void setAutoClaim(boolean autoClaim) {
        this.autoClaim = autoClaim;
    }

    public void setPower(int power) {
        this.power = power;
        this.markDirty();
    }

    public void setMaxPower(int maxPower) {
        this.maxPower = maxPower;
        this.markDirty();
    }

    public void setFaction(UUID factionId, UUID roleId) {
        this.factionId = factionId;
        this.role = roleId;

        FactionIndex.getInstance().updatePlayer(this);

        this.markDirty();
    }

    public void setRole(UUID role) {
        this.role = role;
    }

    /* ========================================= */
    /* Misc
    /* ========================================= */

    public @Nullable ServerPlayer getServerPlayer() {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(this.id);
    }

    public boolean isPlayerOnline() {
        return getServerPlayer() != null;
    }
}
