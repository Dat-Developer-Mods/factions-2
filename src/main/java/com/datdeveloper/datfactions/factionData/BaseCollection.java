package com.datdeveloper.datfactions.factionData;


import com.datdeveloper.datfactions.database.DatabaseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * The baseclass for FactionObject collections
 * @param <Key> The key type for the store
 * @param <CollectionObject> The object being stored in the database
 */
public abstract class BaseCollection<Key, CollectionObject extends DatabaseEntity> {
    Map<Key, CollectionObject> map = new HashMap<>();
    boolean setup = false;

    public Map<Key, CollectionObject> getAll() {
        return map;
    }

    /**
     * Get an object from the collection by its key
     * @param key the key of the CollectionObject
     * @return the object in the collection
     */
    CollectionObject getByKey(Key key) {
        return map.get(key);
    }

    /**
     * Initialise the collection
     */
    abstract void initialise();
}
