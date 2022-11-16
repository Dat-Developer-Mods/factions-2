package com.datdeveloper.datfactions.factionData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FWorldCollection {
    Map<UUID, Faction> factions;
    FWorldCollection instance = new FWorldCollection();

    FWorldCollection() {
        factions = new HashMap<>();
    }

    public FWorldCollection getInstance() {
        return instance;
    }
}
