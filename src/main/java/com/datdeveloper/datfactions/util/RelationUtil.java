package com.datdeveloper.datfactions.util;

import com.datdeveloper.datfactions.factionData.relations.EFactionRelation;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.relations.FactionRelation;

/**
 * Utilities for getting relations between players and factions
 */
public class RelationUtil {
    /**
     * Get the relation between 2 players
     * @param from the player the relation is being tested for
     * @param to the player the relation is to
     * @return The relation between the two players
     */
    public static EFactionRelation getRelation(final FactionPlayer from, final FactionPlayer to) {
        if (!from.hasFaction() || !to.hasFaction()) return EFactionRelation.NEUTRAL;
        return getRelation(from.getFaction(), to.getFaction());
    }

    /**
     * Get the relation between a player and a faction
     * @param from the player the relation is being tested for
     * @param to the faction the relation is to
     * @return The relation between the player and the faction
     */
    public static EFactionRelation getRelation(final FactionPlayer from, final Faction to) {
        if (to == null || !from.hasFaction()) return EFactionRelation.NEUTRAL;
        return getRelation(from.getFaction(), to);
    }

    /**
     * Get the relation between a faction and a player
     * @param from the faction the relation is being tested for
     * @param to the player the relation is to
     * @return The relation between the faction and the player
     */
    public static EFactionRelation getRelation(final Faction from, final FactionPlayer to) {
        if (from == null || !to.hasFaction()) return EFactionRelation.NEUTRAL;
        return getRelation(from, to.getFaction());
    }

    /**
     * Get the relation between 2 factions
     * @param from the faction the relation is being tested for
     * @param to the faction the relation is to
     * @return The relation between the two factions
     */
    public static EFactionRelation getRelation(final Faction from, final Faction to) {
        if (from == null || to == null) return EFactionRelation.NEUTRAL;
        else if (from.equals(to)) return EFactionRelation.SELF;

        final FactionRelation relation = from.getRelation(to);
        if (relation != null) return relation.getRelation();

        return EFactionRelation.NEUTRAL;
    }

    /**
     * Get the mutual relation between 2 factions
     * <br>
     * This will resolve to neutral if they differ or if either faction is null, and the relation if the two factions agree
     * @param faction1 The first faction
     * @param faction2 The second faction
     * @return The mutual relation between the two factions
     */
    public static EFactionRelation getMutualRelation(final Faction faction1, final Faction faction2) {
        if (faction1 == null || faction2 == null) return EFactionRelation.NEUTRAL;
        else if (faction1.equals(faction2)) return EFactionRelation.SELF;

        final FactionRelation relation1 = faction1.getRelation(faction2);
        final FactionRelation relation2 = faction2.getRelation(faction1);

        if (relation1 != null && relation2 != null && relation1.getRelation() == relation2.getRelation()) return relation1.getRelation();
        else return EFactionRelation.NEUTRAL;
    }
}
