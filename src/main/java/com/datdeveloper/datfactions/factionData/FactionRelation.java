package com.datdeveloper.datfactions.factionData;

public class FactionRelation {
    EFactionRelation relation;
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
}
