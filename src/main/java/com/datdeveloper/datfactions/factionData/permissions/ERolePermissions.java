package com.datdeveloper.datfactions.factionData.permissions;

public enum ERolePermissions {
    // Player Management
    INVITE,
    UNINVITE,
    KICK,
    SETRANK,
    PROMOTE,
    DEMOTE,

    // Land
    CLAIM,
    UNCLAIM,
    UNCLAIMALL,
    AUTOCLAIM,

    // Land Access
    CONTAINERS,
    BUILD,

    // Faction Management
    SETNAME,
    SETMOTD,
    DISBAND,

    // RELATION
    ENEMY,
    ALLY,
    NEUTRAL,

    // Chat
    FACTIONCHAT,
    ALLYCHAT,

    // Role
    SETROLE,
    CREATEROLE,
    REMOVEROLE,
    MODIFYROLE,

    // Misc
    HOME,
    SETHOME,

}
