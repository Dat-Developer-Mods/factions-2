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
 * <br>
 * Cancellable, and changes to newPermissions will be reflected
 */
@Cancelable
public class FactionRoleChangePermissionsEvent extends FactionRoleEvent {
    /**
     * The new permissions the role will get
     */
    Set<ERolePermissions> newPermissions;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param role The role changing order
     * @param newPermissions The new permissions of the role
     */
    public FactionRoleChangePermissionsEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionRole role, final Set<ERolePermissions> newPermissions) {
        super(instigator, faction, role);
        this.newPermissions = newPermissions;
    }

    /**
     * Get the new permissions of the role
     * <br>
     * Changes will be reflected
     * @return the new permissions of the role
     */
    public Set<ERolePermissions> getNewPermissions() {
        return newPermissions;
    }

    /**
     * Set the new permissions of the role
     * @param newPermissions the new Permissions
     */
    public void setNewPermissions(final Set<ERolePermissions> newPermissions) {
        this.newPermissions = newPermissions;
    }
}
