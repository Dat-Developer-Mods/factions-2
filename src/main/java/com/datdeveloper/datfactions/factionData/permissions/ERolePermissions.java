package com.datdeveloper.datfactions.factionData.permissions;

/**
 * The available permissions for a role in a faction
 */
public enum ERolePermissions {
    // Player Management
    INVITE("Allows the player to invite players to the faction"),
    UNINVITE("Allows the player to uninvite players from the faction"),
    KICK("Allows the player to kick members below their role from the faction"),
    SETROLE("Allows the player to set the role of other players below their role up to their role (they cannot set players to their role or any higher roles)"),
    PROMOTE("Allows the player to promote other players up to the role below their own"),
    DEMOTE("Allows the player to demote other players below their role"),

    // Land
    CLAIM("Allows the player to claim land"),
    UNCLAIM("Allows the player to unclaim land"),
    UNCLAIMALL("Allows the player to unclaim all the faction's land at once"),
    AUTOCLAIM("Allows the player to use autoclaiming"),

    // Land Access
    CONTAINERS("Allows the player to access containers on the faction land"),
    BUILD("Allows the player to build on the faction's land"),
    INTERACT("Allows the player to interact with blocks (Doors, buttons, furnaces, etc) on the faction's land"),

    // Faction Management
    SETNAME("Allows the player to set the faction's name"),
    SETDESC("Allows the player to set the faction's description"),
    SETMOTD("Allows the player to set the Faction's MOTD"),
    DISBAND("Allows the player to disband the faction"),

    // RELATION
    ENEMY("Allows the player to declare enemies"),
    ALLY("Allows the player to declare allies"),
    TRUCE("ALLows the player to declare truces with other factions"),
    NEUTRAL("Allows the player to declare other factions as neutral"),


    // Chat
    FACTIONCHAT("Allows the player to use the faction's private chat"),
    ALLYCHAT("Allows the player to use the faction's ally chat"),

    // Role
    CREATEROLE("Allows the player to create new roles in the faction"),
    REMOVEROLE("Allows the player to remove roles that are below their own from the faction"),
    MODIFYROLE("Allows the player to modify the permissions of a role (Only with permissions that their role has"),

    // Misc
    HOME("Allows the player to go to the faction's home"),
    SETHOME("Allows the player to set the faction's home");

    public final String description;

    ERolePermissions(final String description) {
        this.description = description;
    }
}
