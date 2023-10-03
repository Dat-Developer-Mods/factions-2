package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a role changes whether it's an admin role
 * @see FactionRoleSetAdminEvent.Pre
 * @see FactionRoleSetAdminEvent.Post
 */
public abstract class FactionRoleSetAdminEvent extends FactionRoleEvent {
    /**
     * The new admin state
     */
    final boolean newAdmin;

    /**
     * @param faction The faction the event is about
     * @param role The role changing admin state
     * @param newAdmin The new admin state of the role
     */
    protected FactionRoleSetAdminEvent(@NotNull final Faction faction, @NotNull final FactionRole role, final boolean newAdmin) {
        super(faction, role);
        this.newAdmin = newAdmin;
    }

    /**
     * Get the new admin state of the role
     * @return the new admin state of the role
     */
    public boolean isNewAdmin() {
        return newAdmin;
    }

    /**
     * Get if the role is an admin before the change
     * @return True if the role is an admin before the change
     */
    public abstract boolean wasAdmin();

    /**
     * Fired before a faction changes whether a role is an admin
     * <br>
     * The purpose of this event is to allow checking a faction role before changes its admin state, for example to prevent
     * roles below a certain level from being admin.
     * <p>
     *     This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. <br>
     *     If the event is cancelled, the role will not change admin state.
     * </p>
     * <p>
     *     When cancelling the event, you should provide a reason with {@link #setDenyReason(Component)} to
     *     allow commands to give a reason for not finishing.<br>
     *     If no reason is given then no feedback will be given to the player
     * </p>
     */
    @Cancelable
    public static class Pre extends FactionRoleSetAdminEvent implements IFactionPreEvent, IFactionEventDenyReason {
        /** The instigator of the action (if there is one) */
        private final ServerPlayer instigator;

        /** A reason for why the event was denied */
        private Component denyReason = null;

        /**
         * @param instigator The player that instigated the event
         * @param faction    The faction the event is about
         * @param role       The role changing admin state
         * @param newAdmin   The new admin state of the role
         */
        public Pre(@Nullable final ServerPlayer instigator, @NotNull final Faction faction, @NotNull final FactionRole role, final boolean newAdmin) {
            super(faction, role, newAdmin);
            this.instigator = instigator;
        }

        /** {@inheritDoc} */
        @Override
        public boolean wasAdmin() {
            return role.isAdministrator();
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
     * Fired after a faction role changes whether it's admin
     * <br>
     * The intention of this event is to allow observing role admin changes to update other resources
     */
    public static class Post extends FactionRoleSetAdminEvent {
        final boolean wasAdmin;

        /**
         * @param faction    The faction the event is about
         * @param role       The role changing admin state
         * @param wasAdmin   Whether the role was previously admin
         * @param newAdmin   The new admin state of the role
         */
        public Post(@NotNull final Faction faction,
                    @NotNull final FactionRole role,
                    final boolean wasAdmin,
                    final boolean newAdmin) {
            super(faction, role, newAdmin);
            this.wasAdmin = wasAdmin;
        }

        @Override
        public boolean wasAdmin() {
            return wasAdmin;
        }
    }
}
