package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.FactionPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

/**
 * Base class for player power change events
 */
public class FactionPlayerPowerChangeEvent extends FactionPlayerEvent {
    /**
     * The other player involved (if there is one)
     */
    @Nullable
    final FactionPlayer otherPlayer;

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
    Map<String, Float> multipliers;

    /**
     * The reason the player changed power
     */
    final EPowerChangeReason reason;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param player The player the event is for
     * @param otherPlayer The other player involved (If there is one)
     * @param basePowerChange The change in power
     * @param baseMaxPowerChange The change in power
     * @param reason The reason for changing power
     */
    public FactionPlayerPowerChangeEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player, @Nullable final FactionPlayer otherPlayer, final int basePowerChange, final int baseMaxPowerChange, final Map<String, Float> multipliers, final EPowerChangeReason reason) {
        super(instigator, player);
        this.otherPlayer = otherPlayer;
        this.basePowerChange = basePowerChange;
        this.baseMaxPowerChange = baseMaxPowerChange;
        this.multipliers = multipliers;
        this.reason = reason;
    }

    /**
     * Get the other player involved (If there is one)
     * @return the other player involved
     */
    public @Nullable FactionPlayer getOtherPlayer() {
        return otherPlayer;
    }

    /**
     * Get the change in power
     * @return the change in power
     */
    public int getBasePowerChange() {
        return basePowerChange;
    }

    /**
     * Set the change in power
     * @param basePowerChange The new change in power
     */
    public void setBasePowerChange(final int basePowerChange) {
        this.basePowerChange = basePowerChange;
    }

    /**
     * Get the change in max power
     * @return the change in max power
     */
    public int getBaseMaxPowerChange() {
        return baseMaxPowerChange;
    }

    /**
     * Set the change in max power
     * @param baseMaxPowerChange The new change in max power
     */
    public void setBaseMaxPowerChange(final int baseMaxPowerChange) {
        this.baseMaxPowerChange = baseMaxPowerChange;
    }

    /**
     * Get the multipliers for the change in power and max power
     * @return the multipliers
     */
    public Map<String, Float> getMultipliers() {
        return multipliers;
    }

    /**
     * Set the multipliers for the change in power and max power
     * @param multipliers The new multipliers
     */
    public void setMultipliers(final Map<String, Float> multipliers) {
        this.multipliers = multipliers;
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
        return multipliers.values().stream().reduce(1.f, (acc, multiplier) -> acc * multiplier);
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
        PASSIVE,
        KILL,
        KILLED
    }

    /**
     * Fired just before a faction player gains power <br>
     * The power added will be validated after this event, meaning changes to the power will be bounded to the players max power,
     * changes to the player's max power will be validated by the configured maximum power, etc
     * <br>
     * Cancellable, and changes to powerChange and maxPowerChange are reflected
     * @see com.datdeveloper.datfactions.api.events.FactionPlayerPowerChangeEvent.PostFactionPlayerPowerChangeEvent
     */
    @Cancelable
    public static class PreFactionPlayerPowerChangeEvent extends FactionPlayerPowerChangeEvent {
        /**
         * @param instigator     The CommandSource that instigated the event
         * @param player         The player the event is for
         * @param otherPlayer    The other player involved (If there is one)
         * @param basePowerChange    The change in power
         * @param baseMaxPowerChange The change in power
         * @param reason         The reason for changing power
         */
        public PreFactionPlayerPowerChangeEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player, @Nullable final FactionPlayer otherPlayer, final int basePowerChange, final int baseMaxPowerChange, final Map<String, Float> multipliers, final EPowerChangeReason reason) {
            super(instigator, player, otherPlayer, basePowerChange, baseMaxPowerChange, multipliers, reason);
        }
    }

    /**
     * Fired just after a faction player gains power <br>
     * The {@link PostFactionPlayerPowerChangeEvent#basePowerChange} and {@link PostFactionPlayerPowerChangeEvent#baseMaxPowerChange} will be the actual change in power that was committed to the player data
     * <br>
     * Not cancellable, and Changes will not be reflected
     */
    public static class PostFactionPlayerPowerChangeEvent extends FactionPlayerPowerChangeEvent {
        /**
         * @param instigator     The CommandSource that instigated the event
         * @param player         The player the event is for
         * @param otherPlayer    The other player involved (If there is one)
         * @param basePowerChange    The change in power
         * @param baseMaxPowerChange The change in power
         * @param reason         The reason for changing power
         */
        public PostFactionPlayerPowerChangeEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player, @Nullable final FactionPlayer otherPlayer, final int basePowerChange, final int baseMaxPowerChange, final Map<String, Float> multipliers, final EPowerChangeReason reason) {
            super(instigator, player, otherPlayer, basePowerChange, baseMaxPowerChange, Collections.unmodifiableMap(multipliers), reason);
        }

        @Override
        public void setBasePowerChange(final int basePowerChange) {
            throw new IllegalArgumentException("You cannot change the basePowerChange in the post event");
        }

        @Override
        public void setBaseMaxPowerChange(final int baseMaxPowerChange) {
            throw new IllegalArgumentException("You cannot change the baseMaxPowerChange in the post event");
        }

        @Override
        public void setMultipliers(final Map<String, Float> multipliers) {
            throw new IllegalArgumentException("You cannot change the multipliers in the post event");
        }
    }
}
