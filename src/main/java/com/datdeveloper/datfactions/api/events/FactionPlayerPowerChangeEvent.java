package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.FactionPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    int powerChange;
    /**
     * The change in max power
     */
    int maxPowerChange;

    /**
     * The reason the player changed power
     */
    final EPowerChangeReason reason;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param player The player the event is for
     * @param otherPlayer The other player involved (If there is one)
     * @param powerChange The change in power
     * @param maxPowerChange The change in power
     * @param reason The reason for changing power
     */
    public FactionPlayerPowerChangeEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player, @Nullable final FactionPlayer otherPlayer, final int powerChange, final int maxPowerChange, final EPowerChangeReason reason) {
        super(instigator, player);
        this.otherPlayer = otherPlayer;
        this.powerChange = powerChange;
        this.maxPowerChange = maxPowerChange;

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
    public int getPowerChange() {
        return powerChange;
    }

    /**
     * Set the change in power
     * @param powerChange The new change in power
     */
    public void setPowerChange(final int powerChange) {
        this.powerChange = powerChange;
    }

    /**
     * Get the change in max power
     * @return the change in max power
     */
    public int getMaxPowerChange() {
        return maxPowerChange;
    }

    /**
     * Set the change in max power
     * @param maxPowerChange The new change in max power
     */
    public void setMaxPowerChange(final int maxPowerChange) {
        this.maxPowerChange = maxPowerChange;
    }

    /**
     * Get the reason the player changed power
     * @return the reason for power change
     */
    public EPowerChangeReason getReason() {
        return reason;
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
         * @param powerChange    The change in power
         * @param maxPowerChange The change in power
         * @param reason         The reason for changing power
         */
        public PreFactionPlayerPowerChangeEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player, @Nullable final FactionPlayer otherPlayer, final int powerChange, final int maxPowerChange, final EPowerChangeReason reason) {
            super(instigator, player, otherPlayer, powerChange, maxPowerChange, reason);
        }
    }

    /**
     * Fired just after a faction player gains power <br>
     * The {@link PostFactionPlayerPowerChangeEvent#powerChange} and {@link PostFactionPlayerPowerChangeEvent#maxPowerChange} will be the actual change in power that was committed to the player data
     * <br>
     * Not cancellable, and Changes will not be reflected
     */
    public static class PostFactionPlayerPowerChangeEvent extends FactionPlayerPowerChangeEvent {
        /**
         * @param instigator     The CommandSource that instigated the event
         * @param player         The player the event is for
         * @param otherPlayer    The other player involved (If there is one)
         * @param powerChange    The change in power
         * @param maxPowerChange The change in power
         * @param reason         The reason for changing power
         */
        public PostFactionPlayerPowerChangeEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player, @Nullable final FactionPlayer otherPlayer, final int powerChange, final int maxPowerChange, final EPowerChangeReason reason) {
            super(instigator, player, otherPlayer, powerChange, maxPowerChange, reason);
        }
    }
}
