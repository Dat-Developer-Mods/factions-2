package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionLevel;
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

    public FactionSetHomeEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final FactionLevel newHomeLevel, final BlockPos newHomePos) {
        super(instigator, faction);
        this.newHomeLevel = newHomeLevel;
        this.newHomePos = newHomePos;
    }

    public FactionLevel getNewHomeLevel() {
        return newHomeLevel;
    }

    public void setNewHomeLevel(final FactionLevel newHomeLevel) {
        this.newHomeLevel = newHomeLevel;
    }

    public BlockPos getNewHomePos() {
        return newHomePos;
    }

    public void setNewHomePos(final BlockPos newHomePos) {
        this.newHomePos = newHomePos;
    }
}
