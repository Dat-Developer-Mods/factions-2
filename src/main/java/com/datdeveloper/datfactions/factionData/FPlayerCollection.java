package com.datdeveloper.datfactions.factionData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FPlayerCollection {
    Map<UUID, FactionPlayer> players;
    FPlayerCollection instance = new FPlayerCollection();

    FPlayerCollection() {
        players = new HashMap<>();
    }

    public FPlayerCollection getInstance() {
        return instance;
    }
}
