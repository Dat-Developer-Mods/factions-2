package com.datdeveloper.datfactions.factionData;

import java.util.Map;
import java.util.UUID;

public class FactionWorld {
    public UUID defaultFaction;

    // World Restrictions
    public boolean allowClaiming;
    public boolean requireConnect;
    public int maxLand;
    public int landCost;
    public int maxClaimRadius;

    // Stealing
    public boolean allowLandSteal;
    public boolean requireLandStealConnect;

    // Misc
    public int teleportDelay;

    Map<ChunkKey, ChunkClaim> claims;
}
