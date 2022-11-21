package com.datdeveloper.datfactions.factionData.permissions;

import com.datdeveloper.datfactions.database.DatabaseEntity;

import java.util.*;

/**
 * A role inside the faction, representing the permissions a player has in the faction
 */
public class FactionRole extends DatabaseEntity {
    /**
     * The role's ID
     */
    UUID id;

    /**
     * The name of the role
     */
    String name;

    /**
     * If the role has total permissions on the faction
     */
    boolean administrator;

    /**
     * The permissions the role has
     */
    Set<ERolePermissions> permissions;

    public FactionRole(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.administrator = false;
        permissions = new HashSet<>();
    }

    /**
     * Copy Constructor
     * @param role The role to copy
     */
    public FactionRole(FactionRole role) {
        this.id = UUID.randomUUID();
        this.name = role.name;
        this.administrator = role.administrator;
        // Enums are immutable, so we can reuse the enum, just not the set
        permissions = new HashSet<>(permissions);
    }

    /* ========================================= */
    /* Getters
    /* ========================================= */

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<ERolePermissions> getPermissions() {
        return permissions;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    /* ========================================= */
    /* Setters
    /* ========================================= */

    public void setName(String name) {
        this.name = name;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    /* ========================================= */
    /* Permission Management
    /* ========================================= */

    public boolean hasPermission(ERolePermissions permission) {
        return administrator || permissions.contains(permission);
    }

    public void addPermission(ERolePermissions permission) {
        this.permissions.add(permission);
    }

    public void removePermission(ERolePermissions permission) {
        this.permissions.remove(permission);
    }

    /* ========================================= */
    /* Default Roles
    /* ========================================= */

    public static List<FactionRole> getDefaultRoles() {
        List<FactionRole> roles = new ArrayList<>();
        roles.add(defaultOwnerRole());
        roles.add(defaultOfficerRole());
        roles.add(defaultMemberRole());
        roles.add(defaultRecruitRole());

        return roles;
    }

    public static FactionRole defaultOwnerRole() {
        FactionRole owner = new FactionRole("Owner");

        owner.administrator = true;

        return owner;
    }

    public static FactionRole defaultOfficerRole() {
        FactionRole officer = new FactionRole("Officer");

        officer.permissions = new HashSet<>(Arrays.asList(
                ERolePermissions.INVITE,
                ERolePermissions.UNINVITE,
                ERolePermissions.KICK,
                ERolePermissions.SETROLE,
                ERolePermissions.PROMOTE,
                ERolePermissions.DEMOTE,

                // Land
                ERolePermissions.CLAIM,
                ERolePermissions.UNCLAIM,
                ERolePermissions.AUTOCLAIM,

                // Land Access
                ERolePermissions.CONTAINERS,
                ERolePermissions.BUILD,
                ERolePermissions.INTERACT,

                // Faction Management
                ERolePermissions.SETMOTD,

                // RELATION
                ERolePermissions.ENEMY,
                ERolePermissions.ALLY,
                ERolePermissions.TRUCE,
                ERolePermissions.NEUTRAL,


                // Chat
                ERolePermissions.FACTIONCHAT,
                ERolePermissions.ALLYCHAT,

                // Misc
                ERolePermissions.HOME,
                ERolePermissions.SETHOME
        ));

        return officer;
    }

    public static FactionRole defaultMemberRole() {
        FactionRole member = new FactionRole("Member");

        member.permissions = new HashSet<>(Arrays.asList(
                // Land
                ERolePermissions.CLAIM,
                ERolePermissions.UNCLAIM,

                // Land Access
                ERolePermissions.CONTAINERS,
                ERolePermissions.BUILD,
                ERolePermissions.INTERACT,

                // Chat
                ERolePermissions.FACTIONCHAT,

                // Misc
                ERolePermissions.HOME
        ));

        return member;
    }

    public static FactionRole defaultRecruitRole() {
        FactionRole recruit = new FactionRole("Recruit");

        recruit.permissions = new HashSet<>(Arrays.asList(
                ERolePermissions.FACTIONCHAT,
                ERolePermissions.HOME
        ));

        return recruit;
    }
}
