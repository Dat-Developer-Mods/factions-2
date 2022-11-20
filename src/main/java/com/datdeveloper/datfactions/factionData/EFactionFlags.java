package com.datdeveloper.datfactions.factionData;

/**
 * Flags that modify the behaviour of a faction
 */
public enum EFactionFlags {
    OPEN(false, "Allows people to join the faction without an invite"),
    FRIENDLYFIRE(false, "Allows faction members to harm eachother"),
    TITLED(false, "Shows faction member's role title in their chat messages"),
    PROTECTED(true, "The members of the faction are protected from harm on their own territory"),
    NOPOWERLOSS(true, "You don't lose power when you die in this zone"),
    PERMANENT(true, "The faction cannot be deleted"),
    SILENT(true, "The faction isn't mentioned when you cross its border"),
    STRONGBORDERS(true, "The faction's chunks cannot be stolen"),
    INFINITEPOWER(true, "The faction has unlimited power"),
    UNLIMITEDLAND(true, "The faction has no limit to the amount of chunks it can own"),
    UNRELATEABLE(true, "The faction cannot have relations"),
    UNCHARTED(true, "The faction does not show up on a map"),
    DEFAULT(true, "The faction is one of the default factions"),
    NODAMAGE(true, "You cannot take damage on this faction's chunks"),
    NOBUILD(true, "Players can't break or place blocks in this region"),
    BONUSPOWER(true, "You lose/gain extra power when you die/kill on this faction's chunks"),
    NOMONSTERS(true, "Monsters are prevented from spawning on this faction's chunks"),
    NOANIMALS(true, "Animals are prevented from spawning on this faction's chunks");

    public final boolean admin;
    public final String description;
    EFactionFlags(boolean admin, String description) {
        this.admin = admin;
        this.description = description;
    }
}
