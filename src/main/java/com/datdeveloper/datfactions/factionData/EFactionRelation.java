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
    ALLY(ChatFormatting.GREEN),
    /**
     * No PVP
     */
    TRUCE(ChatFormatting.DARK_AQUA),
    /**
     * Enemies
     */
    ENEMY(ChatFormatting.DARK_RED),
    /**
     * No Relation
     */
    NEUTRAL(ChatFormatting.DARK_PURPLE),
    /**
     * Same faction
     */
    SELF(ChatFormatting.DARK_GREEN);

    /**
     * The colour representing the relation
     */
    public final ChatFormatting formatting;

    EFactionRelation(final ChatFormatting formatting) {
        this.formatting = formatting;
    }
}
