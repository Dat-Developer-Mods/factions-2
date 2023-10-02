package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction creates a new role
 * @see FactionRoleCreateEvent.Pre
 * @see FactionRoleCreateEvent.Post
 */
@Cancelable
public class FactionRoleCreateEvent extends FactionEvent {
    /**
     * @param faction The faction the event is about
     */
    protected FactionRoleCreateEvent(@NotNull final Faction faction) {
        super(faction);
    }

    /**
     * Fired before a faction creates a new Role
     * <br>
     * The purpose of this event is to allow modifying/checking a faction's new role before it is created. For example,
     * to filter/deny roles with names containing profanity.
     */
    @Cancelable
    public static class Pre extends FactionRoleCreateEvent implements IFactionPreEvent {
        /** The instigator of the action (if there is one) */
        private final ServerPlayer instigator;

        /**
         * The name of the new role
         */
        @NotNull String newRoleName;

        /**
         * The parent to be of the new role
         */
        @NotNull FactionRole newRoleParent;
        /**
         * @param instigator   The player that instigated the event
         * @param faction      The faction the event is about
         * @param newRoleName  The name of the new role
         * @param newRoleParent The parent to be of the new role
         */
        public Pre(@Nullable final ServerPlayer instigator,
                   @NotNull final Faction faction,
                   final String newRoleName,
                   final FactionRole newRoleParent) {
            super(faction);
            this.instigator = instigator;
            this.newRoleName = newRoleName;
            this.newRoleParent = newRoleParent;
        }

        public @NotNull String getNewRoleName() {
            return newRoleName;
        }

        public @NotNull FactionRole getNewRoleParent() {
            return newRoleParent;
        }

        public void setNewRoleName(final String newRoleName) {
            this.newRoleName = newRoleName;
        }

        /**
         * Set the parent of the new role
         * @param newRoleParent The parent of the new role
         * @throws IllegalArgumentException When newRoleParent doesn't belong to the faction
         */
        public void setNewRoleParent(final FactionRole newRoleParent) {
            if (!faction.hasRole(newRoleParent)) {
                throw new IllegalArgumentException("newParent must be a role that belongs to the faction");
            }

            this.newRoleParent = newRoleParent;
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable ServerPlayer getInstigator() {
            return instigator;
        }
    }

    /**
     * Fired after a faction creates a new role
     * <br>
     * The intention of this event is to allow observing the creation of roles to update other resources
     */
    public static class Post extends FactionRoleCreateEvent {
        /**
         * The newly created Faction Role
         */
        @NotNull final FactionRole newRole;
        /**
         * @param faction       The faction the event is about
         * @param newRole       The newly created role
         */
        public Post(@NotNull final Faction faction, @NotNull final FactionRole newRole) {
            super(faction);
            this.newRole = newRole;
        }

        public @NotNull FactionRole getNewRole() {
            return newRole;
        }
    }
}
