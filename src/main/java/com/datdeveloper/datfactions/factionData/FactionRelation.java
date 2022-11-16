package com.datdeveloper.datfactions.factionData;

public class FactionRelation {
    EFactionRelation relation;
    long relationCreation;

    enum EFactionRelation {
        ALLY,
        TRUCE,
        ENEMY
    }
}
