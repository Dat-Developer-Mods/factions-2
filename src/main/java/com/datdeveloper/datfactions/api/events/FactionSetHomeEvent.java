package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionLevel;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a faction sets their home
 * <br>
 * Default checks can be skipped, changes to newHomeLevel and newHomePos will be reflected
 */
@BaseFactionEvent.SkipChecks
public class FactionSetHomeEvent extends FactionEvent {
    /**
     * The new home level
     */
    FactionLevel newHomeLevel;

    /**
     * The new home position
     */
    BlockPos newHomePos;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param newHomeLevel The level the new home is in
     * @param newHomePos The position of the new home
     */
    public FactionSetHomeEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final FactionLevel newHomeLevel, final BlockPos newHomePos) {
        super(instigator, faction);
        this.newHomeLevel = newHomeLevel;
        this.newHomePos = newHomePos;
    }

    /**
     * Get the new home level
     * @return the new home level
     */
    public FactionLevel getNewHomeLevel() {
        return newHomeLevel;
    }

    /**
     * Set the new home level
     * @param newHomeLevel the new home level
     */
    public void setNewHomeLevel(final FactionLevel newHomeLevel) {
        this.newHomeLevel = newHomeLevel;
    }

    /**
     * Get the new home position
     * @return the new home position
     */
    public BlockPos getNewHomePos() {
        return newHomePos;
    }

    /**
     * Set the new home pos
     * @param newHomePos The new home pos
     */
    public void setNewHomePos(final BlockPos newHomePos) {
        this.newHomePos = newHomePos;
    }
}
