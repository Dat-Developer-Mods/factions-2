package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Fired when a faction changes the permissions of a Role
 * Cancellable, and changes to newPermissions will be reflected
 */
@Cancelable
public class FactionRoleChangePermissionsEvent extends FactionRoleEvent {
    /**
     * The new permissions the role will get
     */
    Set<ERolePermissions> newPermissions;

    public FactionRoleChangePermissionsEvent(@Nullable CommandSource instigator, @NotNull Faction faction, @NotNull FactionRole role, Set<ERolePermissions> newPermissions) {
        super(instigator, faction, role);
        this.newPermissions = newPermissions;
    }

    public Set<ERolePermissions> getNewPermissions() {
        return newPermissions;
    }

    public void setNewPermissions(Set<ERolePermissions> newPermissions) {
        this.newPermissions = newPermissions;
    }
}
