package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction changes its name
 * @see FactionChangeNameEvent.Pre
 * @see FactionChangeNameEvent.Post
 */
public abstract class FactionChangeNameEvent extends FactionEvent {
    /** The new name of the faction */
    String newName;

    /**
     * @param faction The faction the event is about
     * @param newName The new name of the faction
     */
    protected FactionChangeNameEvent(@NotNull final Faction faction, final String newName) {
        super(faction);
        this.newName = newName;
    }

    /**
     * Get the original name of the faction
     * @return the previous name
     */
    public abstract String getOldName();

    /**
     * Get the newName
     * @return the new Name
     */
    public String getNewName() {
        return newName;
    }

    /**
     * Set the new name
     * @param newName The value to use for the faction's new name
     */
    public void setNewName(final String newName) {
        this.newName = newName;
    }

    /**
     * Fired before the faction Name changes
     * <br>
     * The purpose of this event is to allow modifying/checking a faction's submitted Name before it is applied. For
     * example, filtering or denying profanity.
     * <p>
     *     After this event, the new name will be checked to ensure it is unique and below the configured maximum length
     * </p>
     * <p>
     *     This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.<br>
     *     If the event is cancelled, the faction's name will not change.
     * </p>
     * <p>
     *     When cancelling the event, you should provide a reason with {@link #setDenyReason(Component)} to
     *     allow commands to give a reason for not finishing.<br>
     *     If no reason is given then no feedback will be given to the player
     * </p>
     */
    @Cancelable
    public static class Pre extends FactionChangeNameEvent implements IFactionPreEvent, IFactionEventDenyReason {
        /** The instigator of the action (if there is one) */
        private final ServerPlayer instigator;

        /** A reason for why the event was denied */
        private Component denyReason = null;

        /**
         * @param instigator The player that instigated the event
         * @param faction    The faction the event is about
         * @param newName    The new name of the faction
         */
        public Pre(@Nullable final ServerPlayer instigator, @NotNull final Faction faction, final String newName) {
            super(faction, newName);
            this.instigator = instigator;
        }

        /** {@inheritDoc} */
        @Override
        public String getOldName() {
            return faction.getName();
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
     * Fired after a faction changes its name
     * <br>
     * The intention of this event is to allow observing changes to the name to update other resources
     */
    public static class Post extends FactionChangeNameEvent {
        /** The old name of the faction */
        final String oldName;

        /**
         * @param faction    The faction the event is about
         * @param newName    The new name of the faction
         */
        public Post(@NotNull final Faction faction, final String oldName, final String newName) {
            super(faction, newName);
            this.oldName = oldName;
        }

        /** {@inheritDoc} */
        @Override
        public String getOldName() {
            return oldName;
        }

        @Override
        public void setNewName(final String newName) {
            throw new UnsupportedOperationException("You cannot change the Name in the post event");
        }
    }
}
