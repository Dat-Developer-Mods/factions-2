package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.FactionPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a faction player gains power
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

    public FactionPlayerPowerChangeEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player, @Nullable final FactionPlayer otherPlayer, final int powerChange) {
        super(instigator, player);
        this.otherPlayer = otherPlayer;
        this.powerChange = powerChange;
    }

    public @Nullable FactionPlayer getOtherPlayer() {
        return otherPlayer;
    }

    public int getPowerChange() {
        return powerChange;
    }

    public void setPowerChange(final int powerChange) {
        this.powerChange = powerChange;
    }

    public enum EPowerGainReason {
        PASSIVE,
        KILL,
        KILLED
    }
}
