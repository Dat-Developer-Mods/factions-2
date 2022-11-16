package com.datdeveloper.datfactions.permissions;

import java.util.List;

public class FactionRole {
    String name;
    boolean administrator;
    List<ERolePermissions> permissions;

    public boolean hasPermission(ERolePermissions permission) {
        return administrator || permissions.contains(permission);
    }
}
