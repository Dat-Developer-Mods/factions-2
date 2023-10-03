package com.datdeveloper.datfactions.exceptions;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionLevel;
import net.minecraft.core.BlockPos;

/**
 * An exception for when an error occurs related to a faction's home
 */
public class FactionHomeLocationException extends RuntimeException {
    final BlockPos chunkPos;
    final FactionLevel level;
    final Faction faction;

    public FactionHomeLocationException(final BlockPos chunkPos, final FactionLevel level, final Faction faction) {
        this.chunkPos = chunkPos;
        this.level = level;
        this.faction = faction;
    }

    public FactionHomeLocationException(final String message, final BlockPos chunkPos, final FactionLevel level, final Faction faction) {
        super(message);
        this.chunkPos = chunkPos;
        this.level = level;
        this.faction = faction;
    }

    public FactionHomeLocationException(final String message, final Throwable cause, final BlockPos chunkPos, final FactionLevel level, final Faction faction) {
        super(message, cause);
        this.chunkPos = chunkPos;
        this.level = level;
        this.faction = faction;
    }

    public FactionHomeLocationException(final Throwable cause, final BlockPos chunkPos, final FactionLevel level, final Faction faction) {
        super(cause);
        this.chunkPos = chunkPos;
        this.level = level;
        this.faction = faction;
    }

    public BlockPos getChunkPos() {
        return chunkPos;
    }

    public FactionLevel getLevel() {
        return level;
    }

    public Faction getFaction() {
        return faction;
    }
}
