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
 * Fired when a faction adds a permission to a Role
 * <br>
 * Cancellable, and changes to permissions will be reflected
 */
@Cancelable
public class FactionRoleAddPermissionsEvent extends FactionRoleEvent {
    /**
     * The permissions being added to the role
     */
    Set<ERolePermissions> permissions;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param role The role gaining the permissions
     * @param permissions The permissions being added to the role
     */
    public FactionRoleAddPermissionsEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionRole role, final Set<ERolePermissions> permissions) {
        super(instigator, faction, role);
        this.permissions = permissions;
    }

    /**
     * Get the new permissions of the role
     * <br>
     * Changes will be reflected
     * @return the new permissions of the role
     */
    public Set<ERolePermissions> getPermissions() {
        return permissions;
    }

    /**
     * Set the permissions being added to the role
     * @param permissions the Permissions being added to the role
     */
    public void setPermissions(final Set<ERolePermissions> permissions) {
        this.permissions = permissions;
    }
}
