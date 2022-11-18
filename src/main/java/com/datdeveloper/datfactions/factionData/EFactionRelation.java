package com.datdeveloper.datfactions.factionData;

import net.minecraft.ChatFormatting;

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

    final ChatFormatting formatting;

    EFactionRelation(ChatFormatting formatting) {
        this.formatting = formatting;
    }

    public ChatFormatting getFormatting() {
        return formatting;
    }
}
