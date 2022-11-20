package com.datdeveloper.datfactions.factionData;

/**
 * Represents a faction relation
 */
public class FactionRelation {
    /**
     * The type of relation
     */
    EFactionRelation relation;

    /**
     * The timestamp of when the relation was made
     */
    long relationCreation;

    public FactionRelation(EFactionRelation relation) {
        this.relation = relation;
        this.relationCreation = System.currentTimeMillis();
    }

    public EFactionRelation getRelation() {
        return relation;
    }

    public long getRelationCreation() {
        return relationCreation;
    }

    public void setRelation(EFactionRelation relation) {
        this.relation = relation;
    }

    public void setRelationCreation(long relationCreation) {
        this.relationCreation = relationCreation;
    }
}
