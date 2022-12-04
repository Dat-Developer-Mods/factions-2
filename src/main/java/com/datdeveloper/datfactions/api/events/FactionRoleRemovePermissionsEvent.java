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
 * Fired when a faction removes permissions from a Role
 * <br>
 * Cancellable, and changes to permissions will be reflected
 */
@Cancelable
public class FactionRoleRemovePermissionsEvent extends FactionRoleEvent {
    /**
     * The permissions being removed from the role
     */
    Set<ERolePermissions> permissions;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param role The role losing the permissions
     * @param permissions The permissions being removed from the role
     */
    public FactionRoleRemovePermissionsEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionRole role, final Set<ERolePermissions> permissions) {
        super(instigator, faction, role);
        this.permissions = permissions;
    }

    /**
     * Get the permissions being removed from the role
     * <br>
     * Changes will be reflected
     * @return the permissions being removed from the role
     */
    public Set<ERolePermissions> getPermissions() {
        return permissions;
    }

    /**
     * Set the permissions being removed from the role
     * @param newPermissions the Permissions being removed from the role
     */
    public void setNewPermissions(final Set<ERolePermissions> newPermissions) {
        this.permissions = newPermissions;
    }
}
