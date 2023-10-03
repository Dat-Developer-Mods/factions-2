package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
     * @param faction The faction the event is about
     * @param role The role changing order
     * @param newParent The new parent of the role
     */
    protected FactionRoleChangeHierarchyEvent(@NotNull final Faction faction, @NotNull final FactionRole role, final FactionRole newParent) {
        super(faction, role);
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
     * <p>
     *     When cancelling the event, you should provide a reason with {@link #setDenyReason(Component)} to
     *     allow commands to give a reason for not finishing.<br>
     *     If no reason is given then no feedback will be given to the player
     * </p>
     */
    @Cancelable
    public static class Pre extends FactionRoleChangeHierarchyEvent implements IFactionPreEvent, IFactionEventDenyReason {
        /** The instigator of the action (if there is one) */
        private final ServerPlayer instigator;

        /** A reason for why the event was denied */
        private Component denyReason = null;

        /**
         * @param instigator The player that instigated the event
         * @param faction    The faction the event is about
         * @param role       The role changing order
         * @param newParent   The new parent of the role
         */
        public Pre(@Nullable final ServerPlayer instigator, @NotNull final Faction faction, @NotNull final FactionRole role, final FactionRole newParent) {
            super(faction, role, newParent);
            this.instigator = instigator;
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

        /** {@inheritDoc} */
        @Override
        public @Nullable ServerPlayer getInstigator() {
            return instigator;
        }

        /** {@inheritDoc} */
        @Override
        public Component getDenyReason() {
            return denyReason;
        }

        /** {@inheritDoc} */
        @Override
        public void setDenyReason(final Component denyReason) {
            this.denyReason = denyReason;
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
         * @param faction    The faction the event is about
         * @param role       The role changing order
         * @param newParent  The new parent of the role
         * @param oldParent  The previous parent of the role
         */
        public Post(@NotNull final Faction faction,
                    @NotNull final FactionRole role,
                    final FactionRole oldParent,
                    final FactionRole newParent) {
            super(faction, role, newParent);
            this.oldParent = oldParent;
        }

        /** {@inheritDoc} */
        @Override
        public FactionRole getOldParent() {
            return oldParent;
        }
    }
}
