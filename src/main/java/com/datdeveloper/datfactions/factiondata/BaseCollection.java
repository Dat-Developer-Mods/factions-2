package com.datdeveloper.datfactions.factiondata;


import com.datdeveloper.datfactions.database.Database;
import com.datdeveloper.datfactions.database.DatabaseEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The baseclass for FactionObject collections
 * @param <K> The key type for the store
 * @param <V> The object being stored in the database
 */
public abstract class BaseCollection<K, V extends DatabaseEntity> {
    protected final Map<K, V> map = new ConcurrentHashMap<>();

    public Map<K, V> getAll() {
        return map;
    }

    /**
     * Get an object from the collection by its key
     * @param key the key of the CollectionObject
     * @return the object in the collection
     */
    public V getByKey(final K key) {
        if (key == null) return null;
        return map.get(key);
    }

    /**
     * Save all the dirty CollectionObjects to the database
     */
    public void saveDirty() {
        for (final V object: map.values()) {
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
