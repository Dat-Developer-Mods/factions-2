package com.datdeveloper.datfactions.database;

import java.io.Serializable;

public abstract class DatabaseEntity implements Serializable {
    transient boolean dirty = false;

    public void markDirty() {
        dirty = true;
    }

    public void markClean() {
        dirty = false;
    }

    public boolean isDirty() {
        return dirty;
    }
}