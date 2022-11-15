package com.datdeveloper.datfactions.permissions;

import java.util.List;

public class FactionRole {
    String name;
    boolean administrator;
    List<RolePermissions> permissions;

    public boolean hasPermission(RolePermissions permission) {
        return administrator || permissions.contains(permission);
    }
}
