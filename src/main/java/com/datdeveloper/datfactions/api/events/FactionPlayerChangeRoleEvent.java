package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a player changes role inside their faction
 * <br>
 * Changes to newRole will be reflected, except for the new owner when the reason is NEW_OWNER
 * The event can only be cancelled if the reason isn't REMOVED or NEW_OWNER
 */
public class FactionPlayerChangeRoleEvent extends FactionPlayerEvent {
    /**
     * The player's new role
     */
    @NotNull
    FactionRole newRole;

    /**
     * The reason the player changed factions
     */
    final EChangeRoleReason reason;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param player The player changing role
     * @param newRole The role in the faction the player will get
     * @param reason The reason the player changed role
     */
    public FactionPlayerChangeRoleEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player, @NotNull final FactionRole newRole, final EChangeRoleReason reason) {
        super(instigator, player);
        this.newRole = newRole;
        this.reason = reason;
    }

    /**
     * Get the new role the player will take
     * @return the new role for the player
     */
    public @NotNull FactionRole getNewRole() {
        return newRole;
    }

    /**
     * Set the role the player will take
     * @param newRole The role the player will take
     */
    public void setNewRole(@NotNull final FactionRole newRole) {
        if (getReason() == EChangeRoleReason.CHANGE_OWNER && this.newRole.equals(getPlayer().getFaction().getOwnerRole())) {
            throw new UnsupportedOperationException("You cannot set the role for the new faction owner when the reason is CHANGE_OWNER");
        }

        this.newRole = newRole;
    }

    /**
     * Get the reason for the role change
     * @return the reason
     */
    public EChangeRoleReason getReason() {
        return reason;
    }

    @Override
    public boolean isCancelable() {
        return getReason() != EChangeRoleReason.REMOVED && getReason() != EChangeRoleReason.CHANGE_OWNER;
    }

    /**
     * The reason the player left the faction
     */
    public enum EChangeRoleReason {
        /** Changed because they were promoted */
        PROMOTE,
        /** Changed because they were demoted */
        DEMOTE,
        /** Changed because their role was set */
        SET,
        /** Changed because their role was removed */
        REMOVED,
        /** Changed because the faction changed owner */
        CHANGE_OWNER
    }
}
