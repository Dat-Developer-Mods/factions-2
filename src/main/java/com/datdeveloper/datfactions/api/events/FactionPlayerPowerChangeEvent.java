package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.FactionPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a faction player gains power
 * <br>
 * Cancellable, and changes to powerChange are reflected
 */
@Cancelable
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
     * The reason the player changed power
     */
    final EPowerChangeReason reason;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param player The player the event is for
     * @param otherPlayer The other player involved (If there is one)
     * @param powerChange The change in power
     * @param reason The reason for changing power
     */
    public FactionPlayerPowerChangeEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player, @Nullable final FactionPlayer otherPlayer, final int powerChange, final EPowerChangeReason reason) {
        super(instigator, player);
        this.otherPlayer = otherPlayer;
        this.powerChange = powerChange;

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
     * @param powerChange The new Change in power
     */
    public void setPowerChange(final int powerChange) {
        this.powerChange = powerChange;
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
}
