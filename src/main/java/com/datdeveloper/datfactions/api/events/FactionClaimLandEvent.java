package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import net.minecraft.commands.CommandSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a faction claims chunks
 * Cancellable, and changes to chunkPos and level will be reflected
 */
@Cancelable
public class FactionClaimLandEvent extends FactionEvent {
    /**
     * The chunk the faction claims
     */
    ChunkPos chunkPos;
    /**
     * The level the chunk is in
     */
    ResourceKey<Level> level;

    /**
     * The current owner of the chunks, if there is one
     */
    @Nullable
    Faction currentOwner;

    public FactionClaimLandEvent(@Nullable CommandSource instigator, @NotNull Faction faction, ChunkPos chunkPos, ResourceKey<Level> level, Faction currentOwner) {
        super(instigator, faction);
        this.chunkPos = chunkPos;
        this.level = level;
        this.currentOwner = currentOwner;
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public void setChunkPos(ChunkPos chunkPos) {
        this.chunkPos = chunkPos;
    }

    public ResourceKey<Level> getLevel() {
        return level;
    }

    public void setLevel(ResourceKey<Level> level) {
        this.level = level;
    }

    public @Nullable Faction getCurrentOwner() {
        return currentOwner;
    }
}
