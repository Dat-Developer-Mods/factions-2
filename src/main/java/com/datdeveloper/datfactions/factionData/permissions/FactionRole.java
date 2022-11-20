package com.datdeveloper.datfactions.factionData.permissions;

import com.datdeveloper.datfactions.database.DatabaseEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
    Set<ERolePermissions> permissions;

    public FactionRole(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.administrator = false;
        permissions = new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    public Set<ERolePermissions> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(ERolePermissions permission) {
        return administrator || permissions.contains(permission);
    }

    public void addPermission(ERolePermissions permission) {
        this.permissions.add(permission);
    }

    public void removePermission(ERolePermissions permission) {
        this.permissions.remove(permission);
    }
}
