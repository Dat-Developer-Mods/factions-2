package com.datdeveloper.datfactions.Util;

import com.datdeveloper.datfactions.factionData.EFactionRelation;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.FactionRelation;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

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
    public static EFactionRelation getRelation(FactionPlayer from, FactionPlayer to) {
        if (from.hasFaction() && to.hasFaction()) return getRelation(from.getFaction(), to.getFaction());
        return EFactionRelation.NEUTRAL;
    }

    /**
     * Get the relation between a player and a faction
     * @param from the player the relation is being tested for
     * @param to the faction the relation is to
     * @return The relation between the player and the faction
     */
    public static EFactionRelation getRelation(FactionPlayer from, Faction to) {
        if (from.hasFaction()) return getRelation(from.getFaction(), to);
        return EFactionRelation.NEUTRAL;
    }

    /**
     * Get the relation between a faction and a player
     * @param from the faction the relation is being tested for
     * @param to the player the relation is to
     * @return The relation between the faction and the player
     */
    public static EFactionRelation getRelation(Faction from, FactionPlayer to) {
        if (to.hasFaction()) return getRelation(from, to.getFaction());
        return EFactionRelation.NEUTRAL;
    }

    /**
     * Get the relation between 2 factions
     * @param from the faction the relation is being tested for
     * @param to the faction the relation is to
     * @return The relation between the two factions
     */
    public static EFactionRelation getRelation(Faction from, Faction to) {
        if (from.getId().equals(to.getId())) return EFactionRelation.SELF;

        FactionRelation relation = from.getRelation(to);
        if (relation != null) return relation.getRelation();

        return EFactionRelation.NEUTRAL;
    }

    /**
     * Wrap a faction name with chat formatting for the relation and a click event for getting info about the faction
     * @param from The faction the relation is from
     * @param to The faction the relation is to (and whose name to display)
     * @return a chat component with the to faction name and the applied formatting
     */
    public static MutableComponent wrapFactionName(Faction from, Faction to) {
        return MutableComponent.create(Component.literal(to.getName()).getContents())
                .withStyle(getRelation(from, to).formatting)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/factions info " + to.getName())));
    }

    /**
     * Wrap a faction name with chat formatting for the relation and a click event for getting info about the faction
     * @param from The player the relation is from
     * @param to The faction the relation is to (and whose name to display)
     * @return a chat component with the to faction name and the applied formatting
     */
    public static MutableComponent wrapFactionName(FactionPlayer from, Faction to) {
        return MutableComponent.create(Component.literal(to.getName()).getContents())
                .withStyle(getRelation(from, to).formatting)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/factions info " + to.getName())));
    }

    /**
     * Wrap a player name with chat formatting for the relation and a click event for getting info about the player
     * @param from The player the relation is from
     * @param to The player the relation is to (and whose name to display)
     * @return a chat component with the to player name and the applied formatting
     */
    public static MutableComponent wrapPlayerName(FactionPlayer from, FactionPlayer to) {

        return MutableComponent.create(Component.literal(to.getLastName()).getContents())
                .withStyle(getRelation(from, to).formatting)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/factions pinfo " + to.getLastName())));
    }

    /**
     * Wrap a player name with chat formatting for the relation and a click event for getting info about the player
     * @param from The faction the relation is from
     * @param to The player the relation is to (and whose name to display)
     * @return a chat component with the to player name and the applied formatting
     */
    public static MutableComponent wrapPlayerName(Faction from, FactionPlayer to) {

        return MutableComponent.create(Component.literal(to.getLastName()).getContents())
                .withStyle(getRelation(from, to).formatting)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/factions pinfo " + to.getLastName())));
    }
}
