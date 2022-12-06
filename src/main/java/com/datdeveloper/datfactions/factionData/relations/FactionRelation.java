package com.datdeveloper.datfactions.factionData.relations;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionCollection;

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
    final EFactionRelation relation;

    /**
     * The timestamp of when the relation was made
     */
    final long relationCreation;

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

    public long getRelationCreation() {
        return relationCreation;
    }
}
