package com.datdeveloper.datfactions.FactionData;

import java.util.UUID;

public class WorldSettings {
    UUID defaultFaction;

    // World Restrictions
    boolean allowClaiming;
    boolean requireConnect;
    int maxLand;
    int landCost;
    int maxClaimRadius;

    // Stealing
    boolean allowLandSteal;
    boolean requireLandStealConnect;

    // Misc
    int teleportDelay;
}
