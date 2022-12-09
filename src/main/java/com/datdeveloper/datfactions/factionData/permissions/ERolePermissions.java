package com.datdeveloper.datfactions.factionData.permissions;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

/**
 * The available permissions for a role in a faction
 */
public enum ERolePermissions {
    // Player Management
    LISTPLAYERS("Allows the player to list the members of the faction"),
    KICK("Allows the player to kick members below their role from the faction"),
    SETROLE("Allows the player to set the role of other players below their role up to their role (they cannot set players to their role or any higher roles)"),
    PROMOTE("Allows the player to promote other players up to the role below their own"),
    DEMOTE("Allows the player to demote other players below their role"),

    // Invites
    INVITE("Allows the player to invite players to the faction"),
    UNINVITE("Allows the player to uninvite players from the faction"),
    INVITELIST("Allows the player to list invites the faction has sent"),

    // Land
    CLAIMONE("Allows the player to claim single chunks"),
    CLAIMSQUARE("Allows the player to claim a square radius of chunks"),
    AUTOCLAIM("Allows the player to use autoclaiming"),
    UNCLAIMONE("Allows the player to unclaim a single chunk at a time"),
    UNCLAIMSQUARE("Allows the player to unclaim a square of chunks at a time"),
    UNCLAIMLEVEL("Allows the player to unclaim all the faction's chunks in a level"),
    UNCLAIMALL("Allows the player to unclaim all the faction's chunks at once"),

    // Land Access
    CONTAINERS("Allows the player to access containers on the faction chunks"),
    BUILD("Allows the player to build on the faction's chunks"),
    INTERACT("Allows the player to interact with blocks (Doors, buttons, furnaces, etc) on the faction's chunks"),

    // Faction Management
    SETNAME("Allows the player to set the faction's name"),
    SETDESC("Allows the player to set the faction's description"),
    SETMOTD("Allows the player to set the Faction's MOTD"),
    DISBAND("Allows the player to disband the faction"),

    // Relation
    RELATIONLIST("Allows the player to list the faction's relations"),
    RELATIONWISHES("Allows the player to list un-reciprocated relations towards your faction"),
    RELATIONALLY("Allows the player to declare allies"),
    RELATIONTRUCE("ALLows the player to declare truces with other factions"),
    RELATIONNEUTRAL("Allows the player to declare other factions as neutral"),
    RELATIONENEMY("Allows the player to declare enemies"),


    // Chat
    FACTIONCHAT("Allows the player to use the faction's private chat"),
    ALLYCHAT("Allows the player to use the faction's ally chat"),

    // Role
    ROLECREATE("Allows the player to create new roles in the faction"),
    ROLEREMOVE("Allows the player to remove roles that are below their own from the faction"),
    ROLELIST("Allows the player to list the roles in the faction"),
    ROLEINFO("Allows the player to get the info of the roles in the faction"),
    ROLEMODIFYPERMISSIONS("Allows the player to modify the permissions of a role (Only roles that are below the player's current role and only with permissions that their role has"),
    ROLERENAME("Allows the player to rename roles (Only roles that are below the player's current role"),
    ROLEREORDER("Allows the player to reorder the roles (Only roles that are below the player's current role)"),

    // Misc
    HOME("Allows the player to go to the faction's home"),
    SETHOME("Allows the player to set the faction's home");

    public final String description;

    ERolePermissions(final String description) {
        this.description = description;
    }



    /**
     * Get a chat component that contains the name of the permission and shows its description on hover
     * @return a chat component representing the permission
     */
    public MutableComponent getChatComponent() {
        return Component.literal(this.name().toLowerCase())
                .withStyle(Style.EMPTY
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.literal(description)
                        ))
                );
    }
}
