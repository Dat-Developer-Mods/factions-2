package com.datdeveloper.datfactions.factionData;

import net.minecraft.ChatFormatting;

/**
 * Represents the relation from one faction to another
 * Also stores the colour representing the relation
 */
public enum EFactionRelation {
    /**
     * Ally chat and no PVP
     */
    ALLY(ChatFormatting.GREEN, "allied"),
    /**
     * No PVP
     */
    TRUCE(ChatFormatting.DARK_AQUA, "in a truce"),
    /**
     * No Relation
     */
    NEUTRAL(ChatFormatting.DARK_PURPLE, "neutral"),
    /**
     * Enemies
     */
    ENEMY(ChatFormatting.DARK_RED, "enemies"),
    /**
     * Same faction
     */
    SELF(ChatFormatting.DARK_GREEN, "That's you dummy");

    /**
     * The colour representing the relation
     */
    public final ChatFormatting formatting;

    public final String adjective;

    EFactionRelation(final ChatFormatting formatting, final String adjective) {
        this.formatting = formatting;
        this.adjective = adjective;
    }
}
