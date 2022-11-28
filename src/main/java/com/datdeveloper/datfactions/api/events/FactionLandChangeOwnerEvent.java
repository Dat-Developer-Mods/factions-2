package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionLevel;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Fired when a faction claims chunks <br>
 * Note that the default checks are performed in low priority, to disable them use {@link #setSkipDefaultChecks(boolean)}
 * You can access the processed results in lowest priority
 * <br>
 * Cancellable, default checks can be skipped, and changes to chunks and level, and claimingFaction will be reflected
 */
@Cancelable
@BaseFactionEvent.SkipChecks
public class FactionLandChangeOwnerEvent extends BaseFactionEvent {
    /**
     * The chunks the faction are claiming
     */
    final List<ChunkPos> chunks;
    /**
     * The level the chunks are in
     */
    FactionLevel level;

    /**
     * The faction claiming the chunk
     */
    @Nullable
    Faction claimingFaction;

    public FactionLandChangeOwnerEvent(@Nullable final CommandSource instigator, final List<ChunkPos> chunks, final FactionLevel level, @Nullable final Faction claimingFaction) {
        super(instigator);
        this.chunks = chunks;
        this.level = level;
        this.claimingFaction = claimingFaction;
    }

    public List<ChunkPos> getChunks() {
        return chunks;
    }

    public FactionLevel getLevel() {
        return level;
    }

    public void setLevel(final FactionLevel level) {
        this.level = level;
    }

    public @Nullable Faction getClaimingFaction() {
        return claimingFaction;
    }

    public void setClaimingFaction(@Nullable final Faction claimingFaction) {
        this.claimingFaction = claimingFaction;
    }
}
