package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.PowerMultiplier;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Events for when a player changes power
 * @see FactionPlayerPowerChangeEvent.Pre
 * @see FactionPlayerPowerChangeEvent.Post
 */
public class FactionPlayerPowerChangeEvent extends FactionPlayerEvent {
    /**
     * The other entity involved (if there is one)
     */
    @Nullable
    final Entity otherEntity;

    /**
     * The change in power
     */
    int basePowerChange;
    /**
     * The change in max power
     */
    int baseMaxPowerChange;

    /**
     * The multipliers for the power change
     */
    List<PowerMultiplier> multipliers;

    /**
     * The reason the player changed power
     */
    final EPowerChangeReason reason;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param player The player the event is for
     * @param otherEntity The other player involved (If there is one)
     * @param basePowerChange The change in power
     * @param baseMaxPowerChange The change in max power
     * @param multipliers The multipliers for the change in power and max power
     * @param reason The reason for changing power
     */
    protected FactionPlayerPowerChangeEvent(@Nullable final CommandSource instigator,
                                         @NotNull final FactionPlayer player,
                                         @Nullable final Entity otherEntity,
                                         final int basePowerChange,
                                         final int baseMaxPowerChange,
                                         final List<PowerMultiplier> multipliers,
                                         final EPowerChangeReason reason) {
        super(instigator, player);
        this.otherEntity = otherEntity;
        this.basePowerChange = basePowerChange;
        this.baseMaxPowerChange = baseMaxPowerChange;
        this.multipliers = multipliers;
        this.reason = reason;
    }

    public @Nullable Entity getOtherEntity() {
        return otherEntity;
    }

    /**
     * Check if the entity that caused the power change is a {@link ServerPlayer}
     * @return True if the other entity is a player
     */
    public boolean isOtherEntityPlayer() {
        return getOtherEntity() instanceof ServerPlayer;
    }

    /**
     * Get the other {@link FactionPlayer}, if the other entity is a {@link ServerPlayer}
     * @return The other faction player, or null
     */
    public @Nullable FactionPlayer getOtherFactionPlayer() {
        if (getOtherEntity() instanceof final ServerPlayer otherPlayer) {
            return FPlayerCollection.getInstance().getPlayer(otherPlayer);
        }

        return null;
    }

    public int getBasePowerChange() {
        return basePowerChange;
    }

    public int getBaseMaxPowerChange() {
        return baseMaxPowerChange;
    }

    /**
     * Get the multipliers for the change in power and max power
     * @return the multipliers
     */
    public List<PowerMultiplier> getMultipliers() {
        return multipliers;
    }

    /**
     * Get the reason the player changed power
     * @return the reason for power change
     */
    public EPowerChangeReason getReason() {
        return reason;
    }

    /**
     * Get the final multiplier made with all the multipliers combined
     * @return the total multiplier
     */
    public float getTotalMultiplier() {
        return multipliers.stream().reduce(1.f, (acc, multiplier) -> acc * multiplier.getMultiplier(), Float::sum);
    }

    /**
     * Get the final value for the power change using the basePowerChange and the total multiplier
     * @return the final power change value
     */
    public int getFinalPowerChange() {
        return (int) Math.floor(getBasePowerChange() * getTotalMultiplier());
    }

    /**
     * Get the final value for the max power change using the baseMaxPowerChange and the total multiplier
     * @return the final max power change value
     */
    public int getFinalMaxPowerChange() {
        return (int) Math.floor(getBaseMaxPowerChange() * getTotalMultiplier());
    }

    /**
     * Reasons for changing power
     */
    public enum EPowerChangeReason {
        /** The player's power changed passively over time */
        PASSIVE,
        /** The player's power changed for killing something */
        KILL,
        /** The player's power changed for being killed */
        KILLED,
        /** The player's power was changed by an admin */
        ADMIN
    }

    /**
     * Fired before a player gains power
     * <br>
     * The purpose of this event is to allow modifying/checking the power gain before it is applied. For example, to add
     * bonus modifiers for exceptional circumstances.
     * <p>
     *     After this event, the total gain in power will be clamped to the player's max power, and the total gain in
     *     max power will be clamped to the configured max player power
     * </p>
     * <p>
     *     This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.<br>
     *     If the event is cancelled, the player's power will not change.
     * </p>
     */
    @Cancelable
    public class Pre extends FactionPlayerPowerChangeEvent {
        /**
         * @param instigator            The CommandSource that instigated the event
         * @param player                The player the event is for
         * @param otherEntity           The other entity involved (If there is one)
         * @param basePowerChange       The change in power
         * @param baseMaxPowerChange    The change in power
         * @param reason                The reason for changing power
         */
        public Pre(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player, @Nullable final Entity otherEntity, final int basePowerChange, final int baseMaxPowerChange, final List<PowerMultiplier> multipliers, final EPowerChangeReason reason) {
            super(instigator, player, otherEntity, basePowerChange, baseMaxPowerChange, multipliers, reason);
        }

        /**
         * Set the change in power
         * @param basePowerChange The new change in power
         */
        public void setBasePowerChange(final int basePowerChange) {
            this.basePowerChange = basePowerChange;
        }

        /**
         * Set the change in max power
         * @param baseMaxPowerChange The new change in max power
         */
        public void setBaseMaxPowerChange(final int baseMaxPowerChange) {
            this.baseMaxPowerChange = baseMaxPowerChange;
        }

        /**
         * Add a multiplier
         * @param multiplier The multiplier to add
         */
        public void addMultiplier(final PowerMultiplier multiplier) {
            this.multipliers.add(multiplier);
        }

        /**
         * Set the multipliers for the change in power and max power
         * @param multipliers The new multipliers
         */
        public void setMultipliers(final List<PowerMultiplier> multipliers) {
            this.multipliers = multipliers;
        }
    }

    /**
     * Fired just after a faction player gains power
     * <br>
     * The intention of this event is to allow observing changes to a player's power in order to update other resources
     */
    public class Post extends FactionPlayerPowerChangeEvent {
        /**
         * @param instigator     The CommandSource that instigated the event
         * @param player         The player the event is for
         * @param otherEntity    The other entity involved (If there is one)
         * @param basePowerChange    The change in power
         * @param baseMaxPowerChange The change in power
         * @param reason         The reason for changing power
         */
        public Post(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player, @Nullable final Entity otherEntity, final int basePowerChange, final int baseMaxPowerChange, final List<PowerMultiplier> multipliers, final EPowerChangeReason reason) {
            super(instigator, player, otherEntity, basePowerChange, baseMaxPowerChange, Collections.unmodifiableList(multipliers), reason);
        }
    }
}
