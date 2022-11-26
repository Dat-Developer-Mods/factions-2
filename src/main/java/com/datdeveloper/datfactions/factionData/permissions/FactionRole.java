package com.datdeveloper.datfactions.factionData.permissions;

import com.datdeveloper.datfactions.database.DatabaseEntity;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionCollection;

import java.util.*;

/**
 * A role inside the faction, representing the permissions a player has in the faction
 */
public class FactionRole extends DatabaseEntity {
    /**
     * The role's ID
     */
    final UUID id;

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

    public FactionRole(final String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.administrator = false;
        permissions = new HashSet<>();
    }

    /**
     * Copy Constructor
     * @param role The role to copy
     */
    public FactionRole(final FactionRole role) {
        this.id = UUID.randomUUID();
        this.name = role.name;
        this.administrator = role.administrator;
        // Enums are immutable, so we can reuse the enum, just not the set
        permissions = new HashSet<>(role.permissions);
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

    public void setName(final String name) {
        this.name = name;
    }

    public void setAdministrator(final boolean administrator) {
        this.administrator = administrator;
    }

    /* ========================================= */
    /* Permission Management
    /* ========================================= */

    /**
     * Check the role has a permission
     * @param permission The permission to test for
     * @return True if the role has the given permission
     */
    public boolean hasPermission(final ERolePermissions permission) {
        return administrator || permissions.contains(permission);
    }

    /**
     * Check the role has any of the given permissions
     * @param permissionTests The permissions to test for
     * @return True if the role has any of the given permissions
     */
    public boolean hasAnyPermissions(final List<ERolePermissions> permissionTests) {
        return administrator || permissionTests.stream().anyMatch(permissions::contains);
    }

    /**
     * Check the role has all the given permissions
     * @param permissionTests The permissions to test for
     * @return True if the role has all the given permissions
     */
    public boolean hadAllPermissions(final List<ERolePermissions> permissionTests) {
        return administrator || permissions.containsAll(permissionTests);
    }

    /**
     * Add a permission to the role
     * Ignores if the role already has the permission
     * @param permission The permission to add
     */
    public void addPermission(final ERolePermissions permission) {
        this.permissions.add(permission);
    }

    /**
     * Remove a permission to the role
     * Ignores if the role does not have the permission
     * @param permission The permission to remove
     */
    public void removePermission(final ERolePermissions permission) {
        this.permissions.remove(permission);
    }

    /* ========================================= */
    /* Default Roles
    /* ========================================= */

    /**
     * Get a list of the default roles
     * This should only be used to generate the template, to get the server's default roles you should look there
     * @see FactionCollection#getTemplate()
     * @return a list of the default roles
     */
    public static List<FactionRole> getDefaultRoles() {
        final List<FactionRole> roles = new ArrayList<>();
        roles.add(defaultOwnerRole());
        roles.add(defaultOfficerRole());
        roles.add(defaultMemberRole());
        roles.add(defaultRecruitRole());

        return roles;
    }

    /**
     * Get the default owner role
     * This should only be used to generate the template, to get the server's default owner role you should look there
     * @see FactionCollection#getTemplate()
     * @see Faction#getOwnerRole()
     * @return the default Owner role
     */
    public static FactionRole defaultOwnerRole() {
        final FactionRole owner = new FactionRole("Owner");

        owner.administrator = true;

        return owner;
    }

    /**
     * Get the default officer role
     * This should only be used to generate the template and is not guaranteed to be one of the default roles
     * @see FactionCollection#getTemplate()
     * @return the default Officer role
     */
    public static FactionRole defaultOfficerRole() {
        final FactionRole officer = new FactionRole("Officer");

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

    /**
     * Get the default member role
     * This should only be used to generate the template and is not guaranteed to be one of the default roles
     * @see FactionCollection#getTemplate()
     * @return the default Member role
     */
    public static FactionRole defaultMemberRole() {
        final FactionRole member = new FactionRole("Member");

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
    /**
     * Get the default recruit role
     * This should only be used to generate the template, to get the server's default recruit role you should look there
     * @see FactionCollection#getTemplate()
     * @see Faction#getRecruitRole()
     * @return the default Recruit role
     */
    public static FactionRole defaultRecruitRole() {
        final FactionRole recruit = new FactionRole("Recruit");

        recruit.permissions = new HashSet<>(Arrays.asList(
                ERolePermissions.FACTIONCHAT,
                ERolePermissions.HOME
        ));

        return recruit;
    }
}
