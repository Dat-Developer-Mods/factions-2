package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a player changes faction
 * @see FactionPlayerChangeMembershipEvent.Pre
 * @see FactionPlayerChangeMembershipEvent.Post
 */
public abstract class FactionPlayerChangeMembershipEvent extends FactionPlayerEvent {
    /**
     * The faction that the player is joining
     */
    @Nullable
    Faction newFaction;

    /**
     * The new role in the faction that the player is joining
     */
    @Nullable
    FactionRole newRole;

    /**
     * The reason the player changed factions
     */
    final EChangeFactionReason reason;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param player The player changing faction
     * @param newFaction The faction the player is changing to
     * @param newRole The role in the faction the player will get
     * @param reason The reason the player changed faction
     */
    protected FactionPlayerChangeMembershipEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player, @Nullable final Faction newFaction, @Nullable final FactionRole newRole, final EChangeFactionReason reason) {
        super(instigator, player);
        this.newFaction = newFaction;
        this.newRole = newRole;
        this.reason = reason;
    }

    /**
     * Get the player's current faction
     * @return The player's current faction
     */
    public abstract @Nullable Faction getOldFaction();

    /**
     * Get the player's current role
     *
     * @return The player's current role
     */
    public abstract @Nullable FactionRole getOldRole();

    /**
     * Get the faction the player is joining
     * @return the faction the player is joining
     */
    public @Nullable Faction getNewFaction() {
        return newFaction;
    }

    /**
     * Get the new role the player will take
     * @return the new role for the player
     */
    public @Nullable FactionRole getNewRole() {
        return newRole;
    }

    /**
     * Get the reason for the membership change
     * @return the reason
     */
    public EChangeFactionReason getReason() {
        return reason;
    }

    /**
     * Fired before a player changes which faction they're in
     * <br>
     * The purpose of this faction is to allow modifying/checking which faction the player will join. For example, the
     * player could, on joining one faction, could be redirected to another.
     * <p>
     *     When the reason is disband, leave, kick, or join, this event {@linkplain HasResult has a result}.<br>
     *     To change the result of this event, use {@link #setResult}.<br>
     *     See the JavaDoc of the {@linkplain EChangeFactionReason reason} as to how the Result will be interpreted
     * </p>
     */
    @HasResult
    public static class Pre extends FactionPlayerChangeMembershipEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param player     The player changing faction
         * @param newFaction The faction the player is changing to
         * @param newRole    The role in the faction the player will get
         * @param reason     The reason the player changed faction
         */
        public Pre(@Nullable final CommandSource instigator,
                   @NotNull final FactionPlayer player,
                   @Nullable final Faction newFaction,
                   @Nullable final FactionRole newRole,
                   final EChangeFactionReason reason) {
            super(instigator, player, newFaction, newRole, reason);

            if (reason.hasPre) throw new UnsupportedOperationException("You cannot use a " + reason.name() + " reason " +
                    "in a pre event");
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable Faction getOldFaction() {
            return player.getFaction();
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable FactionRole getOldRole() {
            return player.getRole();
        }

        /**
         * Set the faction the player will join and the role they will join with
         * @param newFaction the faction the player will join
         * @param newRole The role the player with join with
         * @throws IllegalArgumentException If the faction and the role aren't both null, or both not null, or if the
         * new role does not belong to the faction
         */
        public void setNewFaction(@Nullable final Faction newFaction, @Nullable final FactionRole newRole) {
            if (newFaction == null && newRole != null
                    || newFaction != null && newRole == null) {
                throw new IllegalArgumentException("Both newRole and newFaction must be null, or not null");
            } else if (newFaction != null && newFaction.getRole(newRole.getId()) == null) {
                throw new IllegalArgumentException("newRole must be a role that belongs to newFaction");
            }

            this.newFaction = newFaction;
            this.newRole = newRole;
        }

        /**
         * Set the role the player will take
         * @param newRole The role the player will take
         * @throws IllegalArgumentException If the faction is null, or if the new role does not belong to the faction
         */
        public void setNewRole(@Nullable final FactionRole newRole) {
            if (getNewFaction() == null) {
                throw new IllegalArgumentException("You can't set the newRole when the player isn't joining a faction");
            } if (newRole == null || getNewFaction().getRole(newRole.getId()) == null) {
                throw new IllegalArgumentException("newRole must be a role that belongs to newFaction");
            }

            this.newRole = newRole;
        }

        @Override
        public boolean hasResult() {
            // Only have a result when the reason is one of these
            return getReason().hasResult;
        }
    }

    /**
     * Fired after a player has changed their faction membership
     * <br>
     * The intention of this event is to allow observing player faction member changes to update other resources
     */
    public static class Post extends FactionPlayerChangeMembershipEvent {
        /** The player's previous faction */
        @Nullable
        private final Faction oldFaction;

        /** The player's role in their previous faction */
        @Nullable
        private final FactionRole oldRole;

        /**
         * @param instigator The CommandSource that instigated the event
         * @param player     The player changing faction
         * @param newFaction The faction the player is changing to
         * @param newRole    The role in the faction the player will get
         * @param reason     The reason the player changed faction
         * @param oldFaction
         * @param oldRole
         */
        public Post(@Nullable final CommandSource instigator,
                    @NotNull final FactionPlayer player,
                    @Nullable final Faction newFaction,
                    @Nullable final FactionRole newRole,
                    final EChangeFactionReason reason,
                    final @Nullable Faction oldFaction,
                    final @Nullable FactionRole oldRole) {
            super(instigator, player, newFaction, newRole, reason);
            this.oldFaction = oldFaction;
            this.oldRole = oldRole;
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable Faction getOldFaction() {
            return oldFaction;
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable FactionRole getOldRole() {
            return oldRole;
        }
    }

    /**
     * The reason the player left the faction
     */
    public enum EChangeFactionReason {
        /**
         * Joined because they created the faction
         * <br>
         * Post only
         */
        CREATE(false, false),
        /**
         * Joined because they left a faction being disbanded
         * <br>
         * The pre event can be used for changing which faction the player joins after the disband
         */
        DISBAND(false, true),
        /**
         * Joined because they left their previous faction
         * <br>
         * Pre with result
         * <p>
         * To change the result of the event with this reason, use {@link #setResult}. Results are interpreted in the
         * following manner:
         * </p>
         * <ul>
         *     <li>Allow - The check will succeed, and the player will leave the faction</li>
         *     <li>Default - The player will leave the faction as long as they're not the owner</li>
         *     <li>Deny - The check will fail, and the player will not leave the faction</li>
         * </ul>
         */
        LEAVE(true, true),
        /**
         * Joined because they were kicked from their previous faction
         * <br>
         * Pre with result
         * <p>
         * To change the result of the event with this reason, use {@link #setResult}. Results are interpreted in the
         * following manner:
         * </p>
         * <ul>
         *     <li>Allow - The check will succeed, and the player will be kicked from the faction</li>
         *     <li>Default - The player will be kicked from the faction as long as the player kicking them outranks them</li>
         *     <li>Deny - The check will fail, and the player will not be kicked from the faction</li>
         * </ul>
         */
        KICK(true, true),
        /**
         * Joined because they joined a faction
         * <br>
         * Pre with result
         * <p>
         * To change the result of the event with this reason, use {@link #setResult}. Results are interpreted in the
         * following manner:
         * </p>
         * <ul>
         *     <li>Allow - The check will succeed, and the player will join the faction</li>
         *     <li>Default - The player will join the faction if they have an invite or the faction is open</li>
         *     <li>Deny - The check will fail, and the player will not join the faction</li>
         * </ul>
         */
        JOIN(true, true),

        /**
         * Joined because they are being deleted, and they are being removed from their faction
         * <br>
         * Post only
         */
        DELETE(false, false),

        /**
         * Joined because an admin forced their faction
         * <br>
         * Post only
         */
        ADMIN(false, false);

        /** Whether the event can have a result */
        public final boolean hasResult;

        /** Whether the event has a pre event */
        public final boolean hasPre;

        EChangeFactionReason(final boolean hasResult, final boolean hasPre) {
            this.hasResult = hasResult;
            this.hasPre = hasPre;
        }
    }
}
