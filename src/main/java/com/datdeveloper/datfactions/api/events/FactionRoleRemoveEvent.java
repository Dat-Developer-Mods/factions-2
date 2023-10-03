package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a role is removed
 * @see FactionRoleRemoveEvent.Pre
 * @see FactionRoleRemoveEvent.Post
 */
public class FactionRoleRemoveEvent extends FactionRoleEvent {
    /**
     * @param faction The faction the event is about
     * @param role The role being removed
     */
    protected FactionRoleRemoveEvent(@NotNull final Faction faction, @NotNull final FactionRole role) {
        super(faction, role);
    }

    /**
     * Fired before a faction role is removed
     * <br>
     * The purpose of this event is to allow preventing the removal of a group, for example, if a role is required for
     * use in another mod.
     * <p>
     *     After this event, the removal will be checked to ensure that it contains no children and that it is not the
     *     default role.
     * </p>
     * <p>
     *     This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.<br>
     *     If the event is cancelled, the faction will not be disbanded
     * </p>
     * <p>
     *     When cancelling the event, you should provide a reason with {@link #setDenyReason(Component)} to
     *     allow commands to give a reason for not finishing.<br>
     *     If no reason is given then no feedback will be given to the player
     * </p>
     */
    @Cancelable
    public static class Pre extends FactionRoleRemoveEvent implements IFactionPreEvent, IFactionEventDenyReason {
        /** The instigator of the action (if there is one) */
        private final ServerPlayer instigator;

        /** A reason for why the event was denied */
        private Component denyReason = null;

        /**
         * @param instigator The player that instigated the event
         * @param faction    The faction the event is about
         * @param role       The role being removed
         */
        public Pre(@Nullable final ServerPlayer instigator, @NotNull final Faction faction, @NotNull final FactionRole role) {
            super(faction, role);
            this.instigator = instigator;
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

    public static class Post extends FactionRoleRemoveEvent {
        /**
         * @param faction    The faction the event is about
         * @param role       The role being removed
         */
        public Post(@NotNull final Faction faction, @NotNull final FactionRole role) {
            super(faction, role);
        }
    }
}
