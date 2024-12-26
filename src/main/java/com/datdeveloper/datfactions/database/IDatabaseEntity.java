package com.datdeveloper.datfactions.database;

import java.io.Serializable;

/**
 * Base class for an entity that gets stored
 */
public abstract class IDatabaseEntity implements Serializable {
    transient boolean dirty = false;

    /**
     * Get {@return the entity type}
     * <p>
     * This is used to decide where to store the entity in the database
     */
    public abstract String getEntityType();

    /**
     * Mark the Entry as containing unsaved data so the game knows to save it
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
