package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a faction creates a role
 * <br>
 * Cancellable, and changes to newRoleName and newRoleParent will be reflected
 */
@Cancelable
public class FactionCreateRoleEvent extends FactionEvent {
    /**
     * The name of the new role
     */
    String newRoleName;

    /**
     * The role that the new role will be directly superior to
     */
    FactionRole newRoleParent;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param newRoleName The name of the new role
     * @param newRoleParent The parent of the role
     */
    public FactionCreateRoleEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final String newRoleName, final FactionRole newRoleParent) {
        super(instigator, faction);
        this.newRoleName = newRoleName;
        this.newRoleParent = newRoleParent;
    }

    /**
     * Get the name of the new role
     * @return the name of the new role
     */
    public String getNewRoleName() {
        return newRoleName;
    }

    /**
     * Set the name of the new role
     * @param newRoleName The new role's name
     */
    public void setNewRoleName(final String newRoleName) {
        this.newRoleName = newRoleName;
    }

    /**
     * Get the parent of the new role
     * @return the parent of the new role
     */
    public FactionRole getNewRoleParent() {
        return newRoleParent;
    }

    /**
     * Set the parent of the new role
     * @param newRoleParent The new parent of the role
     * @throws IllegalArgumentException when trying to set the parent to null or the recruit role
     */
    public void setNewRoleParent(final FactionRole newRoleParent) {
        if (newRoleParent == null || !faction.getRoles().contains(newRoleParent) || newRoleParent.equals(faction.getRecruitRole())) throw new IllegalArgumentException("newRoleParent must be a valid role that isn't the recruit role");
        this.newRoleParent = newRoleParent;
    }
}
