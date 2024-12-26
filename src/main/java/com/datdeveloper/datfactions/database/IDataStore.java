package com.datdeveloper.datfactions.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class IDataStore<K, V extends IDatabaseEntity> {
    protected final Map<K, V> entities = new ConcurrentHashMap<>();



}
