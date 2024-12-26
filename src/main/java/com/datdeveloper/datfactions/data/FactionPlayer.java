package com.datdeveloper.datfactions.data;

import com.datdeveloper.datfactions.DatConfig;
import com.datdeveloper.datfactions.database.IDatabaseEntity;
import com.datdeveloper.datmoddingapi.util.DatCodec;
import com.datdeveloper.datmoddingapi.util.ENotificationType;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Representation of a player in the factions system
 */
public class FactionPlayer extends IDatabaseEntity {
    public static final Codec<FactionPlayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(FactionPlayer::getUuid),
            Codec.LONG.fieldOf("lastActiveTime").forGetter(FactionPlayer::getLastActiveTime),
            Codec.INT.fieldOf("power").forGetter(FactionPlayer::getPower),
            Codec.INT.fieldOf("maxPower").forGetter(FactionPlayer::getMaxPower),
            UUIDUtil.CODEC.optionalFieldOf("factionId", null).forGetter(FactionPlayer::getFactionId),
            UUIDUtil.CODEC.optionalFieldOf("factionRole", null).forGetter(FactionPlayer::getRole),
            DatCodec.getEnumCodec(ENotificationType.class).fieldOf("chunkAlertMode")
                    .forGetter(FactionPlayer::getChunkAlertMode)
    ).apply(instance, FactionPlayer::new));

    /** The player's UUID */
    protected final UUID uuid;

    /**
     * The last time the player was active on the server
     */
    protected long lastActiveTime;

    /**
     * The amount of power the player has
     */
    protected int power;

    /**
     * The maximum amount of power the player can have
     */
    protected int maxPower;

    /**
     * The ID of the faction the player belongs to
     */
    protected UUID factionId;

    /**
     * The ID of the role of the player in its faction
     */
    protected UUID role;


    /**
     * Whether the player is currently auto-claiming
     */
    transient boolean autoClaim = false;

    /**
     * The method used to alert the player that it's moved into territory
     */
    ENotificationType chunkAlertMode;

    /**
     * Default constructor for Deserialization
     */
    public FactionPlayer() {
        uuid = null;
    }

    /* ======================================== */
    /* Constructors                             */
    /* ======================================== */

    /**
     * Constructor for a new player
     * @param uuid The UUID of the player
     */
    public FactionPlayer(final UUID uuid, final FactionPlayer template) {
        this.uuid = uuid;
        this.lastActiveTime = System.currentTimeMillis();

        this.power = template.power;
        this.maxPower = template.maxPower;
        this.factionId = template.factionId;
        this.role = template.role;

        this.chunkAlertMode = template.chunkAlertMode;
    }

    public FactionPlayer(final UUID uuid,
                         final long lastActiveTime,
                         final int power,
                         final int maxPower,
                         final UUID factionId,
                         final UUID role,
                         final ENotificationType chunkAlertMode) {
        this.uuid = uuid;
        this.lastActiveTime = lastActiveTime;
        this.power = power;
        this.maxPower = maxPower;
        this.factionId = factionId;
        this.role = role;
        this.chunkAlertMode = chunkAlertMode;
    }

    /**
     * Get {@return the default template for a Faction Player}
     */
    public static FactionPlayer getDefaultTemplate() {
        final FactionPlayer template = new FactionPlayer();
        template.maxPower = 20;
        template.power = template.maxPower;
        template.factionId = null;
        template.role = null;

        template.chunkAlertMode = ENotificationType.TITLE;

        return template;
    }

    /* ======================================== */
    /* Getters                                  */
    /* ======================================== */

    public UUID getUuid() {
        return uuid;
    }

    /**
     * Get {@return the username of the player represented by this FactionPlayer}
     * <p>
     * If the player isn't online, then it will return the name in their cached profile. There are circumstances where
     * this can be cleared, therefore the username will be returned as "UNKNOWN"
     */
    public String getUsername() {
        final ServerPlayer player = getServerPlayer();
        if (player != null) {
            return player.getGameProfile().getName();
        }

        final Optional<GameProfile> gameProfile = ServerLifecycleHooks.getCurrentServer().getProfileCache().get(uuid);
        return gameProfile.isPresent() ? gameProfile.get().getName() : "UNKNOWN";
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

    public UUID getFactionId() {
        return factionId;
    }

    public UUID getRole() {
        return role;
    }

    public boolean isAutoClaim() {
        return autoClaim;
    }

    public ENotificationType getChunkAlertMode() {
        return chunkAlertMode;
    }

    /**
     * Get {@return the {@link ServerPlayer} represented by this FactionPlayer}
     * <p>
     * This will only return a value if the player is currently online, otherwise it will return {@code null}
     */
    @Nullable
    public ServerPlayer getServerPlayer() {
        return ServerLifecycleHooks.getCurrentServer() != null
                ? ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid)
                : null;
    }

    /* ======================================== */
    /* Setters                                  */
    /* ======================================== */

    /**
     * Set the last time the player was active
     * <p>
     * Marks this entity as dirty for resaving
     *
     * @param lastActiveTime The new last time the player was active
     */
    public void setLastActiveTime(final long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
        markDirty();
    }

    /**
     * Set the power of the player, bound by the configured min player power and the player's
     * {@linkplain #getMaxPower() maxPower}
     * <p>
     * Marks this entity as dirty for resaving
     *
     * @param power The new power for the player
     */
    public void setPower(final int power) {
        this.power = Math.clamp(power, DatConfig.getMinPlayerPower(), getMaxPower());
        markDirty();
    }

    /**
     * Set the max power of the player, bound between 0 and the configured max player power
     * <p>
     * If the max power falls below the player's current power, then the player's current power will be set to the new
     * max power.
     * <p>
     * Marks this entity as dirty for resaving
     *
     * @param maxPower The new power for the player
     */
    public void setMaxPower(final int maxPower) {
        this.maxPower = Math.clamp(maxPower, 0, DatConfig.getMaxPlayerPower());
        if (maxPower > power) power = maxPower;

        markDirty();
    }

    /**
     * Sets the player's faction and their role
     * <p>
     * This method should not be called directly as it does not inform the faction
     * <p>
     * Marks this entity as dirty for resaving
     *
     * @param factionId The id of the faction the player is joining
     * @param role The new role of the player in the new faction
     */
    public void setFaction(final UUID factionId, final UUID role) {
        this.factionId = factionId;
        this.role = role;

        markDirty();
    }

    /**
     * Set the role of the player in their faction
     * <p>
     * This method should not be called directly as it does not inform the faction
     * <p>
     * Marks this entity as dirty for resaving
     *
     * @param role The new role of the player in their faction
     */
    public void setRole(final UUID role) {
        this.role = role;
        markDirty();
    }

    /**
     * Set the auto-claiming state of the player
     *
     * @param autoClaim The new auto-claiming state
     */
    public void setAutoClaim(final boolean autoClaim) {
        this.autoClaim = autoClaim;
    }

    /**
     * Set the method for alerting the player when they cross into faction chunks
     * <p>
     * Marks this entity as dirty for resaving
     *
     * @param chunkAlertMode The new Notification type
     */
    public void setChunkAlertMode(final ENotificationType chunkAlertMode) {
        this.chunkAlertMode = chunkAlertMode;
        markDirty();
    }

    /* ======================================== */
    /* Utils                                    */
    /* ======================================== */

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final FactionPlayer that = (FactionPlayer) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String getEntityType() {
        return "player";
    }

    @Override
    public Codec<FactionPlayer> getEntityCodec() {
        return CODEC;
    }
}
