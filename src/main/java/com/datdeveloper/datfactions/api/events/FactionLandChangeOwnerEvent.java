package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionLevel;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fired when a faction claims or unclaims chunks <br>
 * Note that the default checks are performed in low priority, to disable them use {@link #setSkipDefaultChecks(boolean)}
 * You can access the processed results in lowest priority
 * <br>
 * Cancellable, default checks can be skipped, and changes to chunks and level, and claimingFaction will be reflected
 */
@Cancelable
@BaseFactionEvent.SkipChecks
public class FactionLandChangeOwnerEvent extends BaseFactionEvent {
    /**
     * The chunks being claimed
     */
    Set<ChunkPos> chunks;

    /**
     * The level the chunks are in
     */
    FactionLevel level;

    /**
     * The new owner of the chunk
     * <p>
     * If this is null then it can be assumed that the land is being unclaimed
     */
    @Nullable
    Faction newOwner;

    public FactionLandChangeOwnerEvent(@Nullable final CommandSource instigator, final Collection<ChunkPos> chunks, final FactionLevel level, @Nullable final Faction newOwner) {
        super(instigator);
        this.chunks = new HashSet<>(chunks);
        this.level = level;
        this.newOwner = newOwner;
    }

    public Set<ChunkPos> getChunks() {
        return chunks;
    }

    public FactionLevel getLevel() {
        return level;
    }

    public void setLevel(final FactionLevel level) {
        this.level = level;
    }

    public @Nullable Faction getNewOwner() {
        return newOwner;
    }

    public void setNewOwner(@Nullable final Faction newOwner) {
        this.newOwner = newOwner;
    }

    public void setChunks(Set<ChunkPos> chunks) {
        this.chunks = chunks;
    }
}
