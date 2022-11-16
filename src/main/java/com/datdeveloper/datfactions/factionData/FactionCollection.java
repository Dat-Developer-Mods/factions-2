package com.datdeveloper.datfactions.factionData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FactionCollection {
    Map<UUID, Faction> factions;
    FactionCollection instance = new FactionCollection();

    FactionCollection() {
        factions = new HashMap<>();
    }

    public FactionCollection getInstance() {
        return instance;
    }
}
