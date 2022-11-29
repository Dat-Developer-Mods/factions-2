package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionLevel;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Fired when a faction claims or unclaims chunks <br>
 * Note that the default checks are performed in {@link EventPriority#LOW} priority, to disable them use {@link #setSkipDefaultChecks(boolean)}
 * You can access the processed results in the {@link EventPriority#LOWEST} priority
 * <br>
 * Cancellable, default checks can be skipped, and changes to chunks and level, and claimingFaction will be reflected
 */
@Cancelable
@BaseFactionEvent.SkipChecks
public class FactionLandChangeOwnerEvent extends BaseFactionEvent {
    /**
     * The chunks being claimed
     */
    @NotNull
    Set<ChunkPos> chunks;

    /**
     * The level the chunks are in
     */
    @NotNull
    FactionLevel level;

    /**
     * The new owner of the chunk
     * <p>
     * If this is null then it can be assumed that the land is being unclaimed
     */
    @Nullable
    Faction newOwner;

    public FactionLandChangeOwnerEvent(@Nullable final CommandSource instigator, @NotNull final Collection<ChunkPos> chunks, final @NotNull FactionLevel level, @Nullable final Faction newOwner) {
        super(instigator);
        this.chunks = new HashSet<>(chunks);
        this.level = level;
        this.newOwner = newOwner;
    }

    public @NotNull Set<ChunkPos> getChunks() {
        return chunks;
    }

    public void setChunks(final @NotNull Set<ChunkPos> chunks) {
        this.chunks = chunks;
    }

    public @NotNull FactionLevel getLevel() {
        return level;
    }

    public void setLevel(final @NotNull FactionLevel level) {
        this.level = level;
    }

    public @Nullable Faction getNewOwner() {
        return newOwner;
    }

    public void setNewOwner(@Nullable final Faction newOwner) {
        this.newOwner = newOwner;
    }
}
