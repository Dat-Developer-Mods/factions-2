package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionLevel;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.level.ChunkPos;
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
 * Changes to claimingFaction will be reflected <br>
 * Will only be cancellable, will only be able to skip default checks, and changes to level and chunks will only be reflected if {@link #reason} isn't {@link EChangeOwnerReason#DISBAND}
 */
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
     * The new owner of the chunks
     * <p>
     * If this is null then it can be assumed that the land is being unclaimed
     */
    @Nullable
    Faction newOwner;

    /**
     * The reason the chunks changed ownership
     */
    final EChangeOwnerReason reason;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param chunks The chunks that are changing owner
     * @param level The level containing the chunks
     * @param newOwner The new owner of the chunks
     */
    public FactionLandChangeOwnerEvent(@Nullable final CommandSource instigator, @NotNull final Collection<ChunkPos> chunks, final @NotNull FactionLevel level, @Nullable final Faction newOwner, final EChangeOwnerReason reason) {
        super(instigator);
        this.chunks = new HashSet<>(chunks);
        this.level = level;
        this.newOwner = newOwner;
        this.reason = reason;
    }

    /**
     * Get the chunks that are changing owner
     * <br>
     * Changes will be reflected
     * @return the chunks that are changing owner
     */
    public @NotNull Set<ChunkPos> getChunks() {
        return chunks;
    }

    /**
     * Set the chunks that are changing owner
     * @param chunks the chunks that are changing owner
     */
    public void setChunks(final @NotNull Set<ChunkPos> chunks) {
        if (getReason() == EChangeOwnerReason.DISBAND) throw new UnsupportedOperationException("Cannot set chunks when reason is set to DISBAND");
        this.chunks = chunks;
    }

    /**
     * Get the level containing the chunks are changing owner
     * @return the level containing the chunks that are changing owner
     */
    public @NotNull FactionLevel getLevel() {
        return level;
    }

    /**
     * Set the level containing the chunks that are being claimed
     * @param level the level containing the chunks that are changing owner
     */
    public void setLevel(final @NotNull FactionLevel level) {
        if (getReason() == EChangeOwnerReason.DISBAND) throw new UnsupportedOperationException("Cannot set level when reason is set to DISBAND");
        this.level = level;
    }

    /**
     * Get the new owner of the chunks
     * @return the new owner of the chunks
     */
    public @Nullable Faction getNewOwner() {
        return newOwner;
    }

    /**
     * Set the new owner of the chunks
     * @param newOwner the new owner of the chunks
     */
    public void setNewOwner(@Nullable final Faction newOwner) {
        this.newOwner = newOwner;
    }

    /**
     * Get the reason the chunks change owner
     * @return the reason for the chunks changing owner
     */
    public EChangeOwnerReason getReason() {
        return reason;
    }

    public enum EChangeOwnerReason {
        CLAIM,
        UNCLAIM,
        DISBAND
    }

    @Override
    public boolean canSkipDefaultChecks() {
        return getReason() != EChangeOwnerReason.DISBAND;
    }

    @Override
    public boolean isCancelable() {
        return getReason() != EChangeOwnerReason.DISBAND;
    }
}
