package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction creates a new role
 */
@Cancelable
public class FactionRoleCreateEvent extends FactionEvent {
    /**
     * The name of the new role
     */
    String newRoleName;

    /**
     * The parent to be of the new role
     */
    FactionRole newRoleParent;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param newRoleName The name of the new role
     * @param newRoleIndex The index to be of the new role
     */
    public FactionRoleCreateEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final String newRoleName, final int newRoleIndex) {
        super(instigator, faction);
        this.newRoleName = newRoleName;
        this.newRoleIndex = newRoleIndex;
    }

    public String getNewRoleName() {
        return newRoleName;
    }

    public int getNewRoleIndex() {
        return newRoleIndex;
    }

    /**
     * Fired before a faction creates a new Role
     * <br>
     * The purpose of this event is to allow modifying/checking a faction's new role
     */
    public static class Pre extends FactionRoleCreateEvent {
        /**
         * @param instigator   The CommandSource that instigated the event
         * @param faction      The faction the event is about
         * @param newRoleName  The name of the new role
         * @param newRoleIndex The index to be of the new role
         */
        public Pre(@Nullable final CommandSource instigator,
                   @NotNull final Faction faction,
                   final String newRoleName,
                   final int newRoleIndex) {
            super(instigator, faction, newRoleName, newRoleIndex);
        }

    public void setNewRoleName(final String newRoleName) {
        this.newRoleName = newRoleName;
    }

    /**
     * Set the new index of the role
     * @param newRoleIndex The new index of the role
     * @throws IllegalArgumentException When the index is less than 1 (since the owner is 0 and cannot be replaced)
     * or greater than the number of existing roles
     */
    public void setNewRoleIndex(final int newRoleIndex) {
        if (newRoleIndex < 1 || newRoleIndex > faction.getRoles().size()) {
            throw new IllegalArgumentException("The index must be greater than 0 (cannot replace the owner) and"
                    + " less than " + faction.getRoles().size() + " (the number of roles there are)");
        }
        this.newRoleIndex = newRoleIndex;
    }
    }
}
