package com.datdeveloper.datfactions.factiondata;

import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;

/**
 * The chatmode the player has
 */
public enum EFPlayerChatMode {
    /** Regular Chat */
    PUBLIC(null),
    /** Chats deliver only to other faction members */
    FACTION(ERolePermissions.FACTIONCHAT),
    /** Chats deliver only to other faction members and allies */
    ALLY(ERolePermissions.ALLYCHAT);

    public final ERolePermissions requiredPermission;

    EFPlayerChatMode(final ERolePermissions requiredPermission) {
        this.requiredPermission = requiredPermission;
    }
}
