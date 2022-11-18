package com.datdeveloper.datfactions.factionData;

import java.util.UUID;

public class ChunkClaim {
    UUID factionId;
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
