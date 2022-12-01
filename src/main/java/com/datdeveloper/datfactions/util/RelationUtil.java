package com.datdeveloper.datfactions.util;

import com.datdeveloper.datfactions.factionData.EFactionRelation;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.FactionRelation;

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
        if (from.hasFaction() && to.hasFaction()) return getRelation(from.getFaction(), to.getFaction());
        return EFactionRelation.NEUTRAL;
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

        final FactionRelation relation = from.getRelation(to);
        if (relation != null) return relation.getRelation();

        return EFactionRelation.SELF;
    }
}
