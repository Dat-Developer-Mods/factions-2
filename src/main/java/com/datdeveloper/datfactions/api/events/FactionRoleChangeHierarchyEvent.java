package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction restructures its roles
 * @see FactionRoleChangeHierarchyEvent.Pre
 * @see FactionRoleChangeHierarchyEvent.Post
 */
public abstract class FactionRoleChangeHierarchyEvent extends FactionRoleEvent {
    /**
     * The new parent of the role
     */
    FactionRole newParent;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param role The role changing order
     * @param newParent The new parent of the role
     */
    protected FactionRoleChangeHierarchyEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionRole role, final FactionRole newParent) {
        super(instigator, faction, role);
        this.newParent = newParent;
    }

    /**
     * Get the current parent of the role
     * @return The previous parent of the role
     */
    public abstract FactionRole getOldParent();

    public FactionRole getNewParent() {
        return newParent;
    }

    /**
     * Fired before a faction's roles are reordered
     * <br>
     * The purpose of this event is to allow modifying/checking when roles are reordered. For example, a role could
     * be denied being restructured to below a certain role.
     * <p>
          This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. <br>
          If the event is cancelled, the role's position in the hierarchy will not change.
      </p>
     */
    @Cancelable
    public static class Pre extends FactionRoleChangeHierarchyEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param role       The role changing order
         * @param newParent   The new parent of the role
         */
        public Pre(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionRole role, final FactionRole newParent) {
            super(instigator, faction, role, newParent);
        }

        /**
         * Set the new parent of the role
         * @param newParent The new parent
         * @throws IllegalArgumentException When newParent doesn't belong to the faction
         */
        public void setNewParent(final FactionRole newParent) {
            if (!faction.hasRole(newParent)) {
                throw new IllegalArgumentException("newParent must be a role that belongs to the faction");
            }

            this.newParent = newParent;
        }

        /** {@inheritDoc} */
        @Override
        public FactionRole getOldParent() {
            return role.getParent();
        }
    }

    /**
     * Fired after a faction's roles are restructured.
     * <br>
     * The intention of this event is to allow observing faction role order changes to update other resources.
     */
    public static class Post extends FactionRoleChangeHierarchyEvent {
        /** The previous parent of the role */
        private final FactionRole oldParent;

        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param role       The role changing order
         * @param newParent  The new parent of the role
         * @param oldParent  The previous parent of the role
         */
        public Post(@Nullable final CommandSource instigator,
                       @NotNull final Faction faction,
                       @NotNull final FactionRole role,
                       final FactionRole oldParent,
                       final FactionRole newParent) {
            super(instigator, faction, role, newParent);
            this.oldParent = oldParent;
        }

        /** {@inheritDoc} */
        @Override
        public FactionRole getOldParent() {
            return oldParent;
        }
    }
}
