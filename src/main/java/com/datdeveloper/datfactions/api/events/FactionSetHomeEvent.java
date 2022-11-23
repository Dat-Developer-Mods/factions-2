package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a faction sets their home
 * Changes to newHomeLevel and newHomePos will be reflected
 */
public class FactionSetHomeEvent extends FactionEvent {
    /**
     * The new home level
     */
    ResourceKey<Level> newHomeLevel;

    /**
     * The new home position
     */
    BlockPos newHomePos;

    public FactionSetHomeEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final ResourceKey<Level> newHomeLevel, final BlockPos newHomePos) {
        super(instigator, faction);
        this.newHomeLevel = newHomeLevel;
        this.newHomePos = newHomePos;
    }

    public ResourceKey<Level> getNewHomeLevel() {
        return newHomeLevel;
    }

    public void setNewHomeLevel(final ResourceKey<Level> newHomeLevel) {
        this.newHomeLevel = newHomeLevel;
    }

    public BlockPos getNewHomePos() {
        return newHomePos;
    }

    public void setNewHomePos(final BlockPos newHomePos) {
        this.newHomePos = newHomePos;
    }
}
