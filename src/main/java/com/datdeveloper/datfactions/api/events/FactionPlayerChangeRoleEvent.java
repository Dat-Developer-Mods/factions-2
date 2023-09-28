package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a player changes role in a faction
 *
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
    protected FactionPlayerChangeRoleEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player, @NotNull final FactionRole newRole, final EChangeRoleReason reason) {
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
     * Get the faction the player belongs to
     * @return The player's faction
     */
    public @NotNull Faction getPlayerFaction() {
        return player.getFaction();
    }

    /**
     * Get the reason for the role change
     * @return the reason
     */
    public EChangeRoleReason getReason() {
        return reason;
    }

    /**
     * Fired before a player changes their role in a faction
     * <br>
     * The purpose of this event is to allow modifying/checking a player's role change. For example, to ensure that a
     * player of a specific rank in a server cannot hold a certain position in the faction.
     * <p>After this event, the new instigator will be checked to see if they have the authority to promote the player</p>
     * <p>
     *     This event is {@linkplain Cancelable cancellable} (Depending on the reason), and does not
     *     {@linkplain HasResult have a result}.<br>
     *     If the event is cancelled, the player's role will not change.
     * </p>
     */
    @Cancelable
    public class Pre extends FactionPlayerChangeRoleEvent {

        /**
         * @param instigator The CommandSource that instigated the event
         * @param player     The player changing role
         * @param newRole    The role in the faction the player will get
         * @param reason     The reason the player changed role
         */
        public Pre(@Nullable final CommandSource instigator,
                   @NotNull final FactionPlayer player,
                   @NotNull final FactionRole newRole,
                   final EChangeRoleReason reason) {
            super(instigator, player, newRole, reason);
        }

        /**
         * Set the role the player will take
         * @param newRole The role the player will take
         */
        public void setNewRole(@NotNull final FactionRole newRole) {
            if (player.getFaction().getRoles().stream().noneMatch(role -> role.equals(newRole))) {
                throw new IllegalArgumentException("newRole must be a role that belongs to the player's faction");
            }

            this.newRole = newRole;
        }

        @Override
        public boolean isCancelable() {
            return getReason().cancelable;
        }
    }

    /**
     * Fired after a player changes its role
     * <br>
     * The intention of this event is to allow observing changes to player roles to update other resources
     */
    public class Post extends FactionPlayerChangeRoleEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param player     The player changing role
         * @param newRole    The role in the faction the player will get
         * @param reason     The reason the player changed role
         */
        public Post(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player, @NotNull final FactionRole newRole, final EChangeRoleReason reason) {
            super(instigator, player, newRole, reason);
        }
    }

    /**
     * The reason the player left the faction
     */
    public enum EChangeRoleReason {
        /** Changed because they were promoted */
        PROMOTE(true, true),
        /** Changed because they were demoted */
        DEMOTE(true, true),
        /** Changed because their role was set */
        SET(true, true),
        /** Changed because their role was removed */
        REMOVED(false, true),
        /** Changed because the faction changed owner */
        CHANGE_OWNER(true, true),
        /**
         * Changed because an admin set it
         * <br>
         * Post only
         **/
        ADMIN(true, false);

        /** Whether the event is cancelable */
        public final boolean cancelable;

        /** True if the reason does not have a pre event */
        public final boolean hasPre;

        EChangeRoleReason(final boolean cancelable, final boolean hasPre) {
            this.cancelable = cancelable;
            this.hasPre = hasPre;
        }
    }
}
