package com.datdeveloper.datfactions.database;

import java.io.Serializable;

public abstract class DatabaseEntity implements Serializable {
    transient boolean dirty = false;

    /**
     * Mark the Entity as containing unsaved data so the game knows to save it
     */
    public void markDirty() {
        dirty = true;
    }

    /**
     * Mark the Entity as not containing unsaved data so the game knows not to save it
     */
    public void markClean() {
        dirty = false;
    }

    /**
     * Check if the Entity contains unsaved data
     * @return true if the Entity contains unsaved data
     */
    public boolean isDirty() {
        return dirty;
    }
}