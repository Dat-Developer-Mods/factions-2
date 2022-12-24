package com.datdeveloper.datfactions.factionData;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

/**
 * Flags that modify the behaviour of a faction
 */
public enum EFactionFlags {
    OPEN(false, "Allows people to join the faction without an invite"),
    FRIENDLYFIRE(false, "Allows faction members to harm each other"),
    TITLED(false, "Shows faction member's role title in their chat messages"),
    OPENBUILD(false, "Allows non-members to build on this faction's chunks"),
    PROTECTED(true, "The members of the faction are protected from harm on their own chunks"),
    TOTALPROTECTION(true, "All players are protected from harm on this faction's chunks"),
    NOPOWERLOSS(true, "You don't lose power when you die in this faction's land"),
    PERMANENT(true, "The faction cannot be deleted (Not even by admins)"),
    SILENT(true, "The faction isn't mentioned when you cross its border"),
    WEAKBORDERS(true, "Allows anyone to claim this faction's land"),
    STRONGBORDERS(true, "The faction's chunks cannot be stolen"),
    INFINITEPOWER(true, "The faction has unlimited power"),
    UNLIMITEDLAND(true, "The faction has no limit to the amount of chunks it can own (Note this does not prevent stealing)"),
    UNRELATEABLE(true, "The faction cannot have relations"),
    UNCHARTED(true, "The faction does not show up on a map"),
    ANONYMOUS(true, "The members of the faction are anonymous"),
    DEFAULT(true, "The faction is one of the default factions"),
    BONUSPOWER(true, "You gain extra power when you kill on this faction's chunks"),
    NOMONSTERS(true, "Monsters are prevented from spawning on this faction's chunks"),
    NOANIMALS(true, "Animals are prevented from spawning on this faction's chunks"),
    EXPLOSIONPROOF(true, "Explosions do not affect blocks in chunks owned by this faction (Only work for explosives that use the vanilla block breaking system)"),
    INFLAMMABLE(true, "Fire does not affect blocks in chunks owned by this faction");

    public final boolean admin;
    public final String description;
    EFactionFlags(final boolean admin, final String description) {
        this.admin = admin;
        this.description = description;
    }

    /**
     * Get a chat component that contains the name of the flag and shows its description on hover
     * @return a chat component representing the flag
     */
    public MutableComponent getChatComponent() {
        final MutableComponent component = MutableComponent.create(Component.literal(this.name().toLowerCase()).getContents());
        return component.withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(description))));
    }
}
