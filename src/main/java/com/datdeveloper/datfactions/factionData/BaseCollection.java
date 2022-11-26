package com.datdeveloper.datfactions.factionData;


import com.datdeveloper.datfactions.database.Database;
import com.datdeveloper.datfactions.database.DatabaseEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The baseclass for FactionObject collections
 * @param <Key> The key type for the store
 * @param <CollectionObject> The object being stored in the database
 */
public abstract class BaseCollection<Key, CollectionObject extends DatabaseEntity> {
    final Map<Key, CollectionObject> map = new ConcurrentHashMap<>();

    public Map<Key, CollectionObject> getAll() {
        return map;
    }

    /**
     * Get an object from the collection by its key
     * @param key the key of the CollectionObject
     * @return the object in the collection
     */
    public CollectionObject getByKey(final Key key) {
        return map.get(key);
    }

    /**
     * Save all the dirty CollectionObjects to the database
     */
    public void saveDirty() {
        for (final Key key : map.keySet()) {
            final CollectionObject object = map.get(key);

            if (object.isDirty()) {
                Database.instance.storeEntity(object);
                object.markClean();
            }
        }
    }

    /**
     * Initialise the collection
     * Loads the collection content from the database and sets up
     */
    public abstract void initialise();

    /**
     * Uninitialise the collection
     * Saves all to the database and clears stores
     */
    public void uninitialise() {
        saveDirty();
        map.clear();
    }
}
