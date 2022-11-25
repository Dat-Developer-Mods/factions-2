package com.datdeveloper.datfactions.factionData;

import java.util.UUID;

/**
 * Represents a faction relation
 */
public class FactionRelation {
    /**
     * The ID of the faction the relation is with
     */
    final UUID factionID;
    /**
     * The type of relation
     */
    EFactionRelation relation;

    /**
     * The timestamp of when the relation was made
     */
    long relationCreation;

    public FactionRelation(final UUID factionId, final EFactionRelation relation) {
        this.factionID = factionId;
        this.relation = relation;
        this.relationCreation = System.currentTimeMillis();
    }

    /* ========================================= */
    /* Getters
    /* ========================================= */

    public UUID getFactionID() {
        return factionID;
    }

    /**
     * Get the faction the relation is with
     * @return The faction the relation is with
     */
    public Faction getFaction() {
        return FactionCollection.getInstance().getByKey(this.factionID);
    }

    public EFactionRelation getRelation() {
        return relation;
    }

    /* ========================================= */
    /* Setters
    /* ========================================= */

    public long getRelationCreation() {
        return relationCreation;
    }

    public void setRelation(final EFactionRelation relation) {
        this.relation = relation;
    }

    public void setRelationCreation(final long relationCreation) {
        this.relationCreation = relationCreation;
    }
}
