package com.datdeveloper.datfactions.factionData;

import java.util.UUID;

/**
 * An object that represents a claim on a chunk
 */
public class ChunkClaim {
    /**
     * The faction that claimed the chunk
     */
    UUID factionId;

    /**
     * The timestamp of when the chunk was claimed
     */
    long claimTime;

    public ChunkClaim(UUID factionId) {
        this.factionId = factionId;
        claimTime = System.currentTimeMillis();
    }

    public UUID getFactionId() {
        return factionId;
    }

    public long getClaimTime() {
        return claimTime;
    }
}
