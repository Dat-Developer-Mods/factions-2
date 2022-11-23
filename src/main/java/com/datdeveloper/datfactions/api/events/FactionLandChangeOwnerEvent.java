package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionLevel;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a faction claims chunks
 * Cancellable, and changes to chunkPos and level, and newOwner will be reflected
 */
@Cancelable
public class FactionLandChangeOwnerEvent extends BaseFactionEvent {
    /**
     * The chunk the faction claims
     */
    ChunkPos chunkPos;
    /**
     * The level the chunk is in
     */
    FactionLevel level;

    /**
     * The previous owner of the chunk
     */
    @Nullable
    final Faction oldOwner;

    /**
     * The new owner of the chunk
     */
    @Nullable
    Faction newOwner;

    public FactionLandChangeOwnerEvent(@Nullable final CommandSource instigator, final ChunkPos chunkPos, final FactionLevel level, @Nullable final Faction oldOwner, @Nullable final Faction newOwner) {
        super(instigator);
        this.chunkPos = chunkPos;
        this.level = level;
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public void setChunkPos(final ChunkPos chunkPos) {
        this.chunkPos = chunkPos;
    }

    public FactionLevel getLevel() {
        return level;
    }

    public void setLevel(final FactionLevel level) {
        this.level = level;
    }

    public @Nullable Faction getOldOwner() {
        return oldOwner;
    }

    public @Nullable Faction getNewOwner() {
        return newOwner;
    }

    public void setNewOwner(@Nullable final Faction newOwner) {
        this.newOwner = newOwner;
    }
}
