package com.datdeveloper.datfactions.factionData;


import com.datdeveloper.datfactions.database.DatabaseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class BaseCollection<CollectionObject extends DatabaseEntity> {
    Map<UUID, CollectionObject> map = new HashMap<>();
    boolean setup = false;

    CollectionObject getByID(UUID id) {
        return map.get(id);
    }

    abstract void initialise();
}
