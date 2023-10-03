package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction changes its description
 * @see FactionChangeDescriptionEvent.Pre
 * @see FactionChangeDescriptionEvent.Post
 */
public abstract class FactionChangeDescriptionEvent extends FactionEvent {
    /** The new description for the faction */
    protected String newDescription;

    /**
     * @param faction The faction the event is about
     * @param newDescription The description the faction is changing to
     */
    protected FactionChangeDescriptionEvent(@NotNull final Faction faction, final String newDescription) {
        super(faction);
        this.newDescription = newDescription;
    }

    /**
     * Get the original description of the faction
     * @return the previous description
     */
    public abstract String getOldDescription();

    /**
     * Get the description the faction is changing to
     * @return the new Description
     */
    public String getNewDescription() {
        return newDescription;
    }

    /**
     * Fired before the faction description changes
     * <br>
     * The purpose of this event is to allow modifying/checking a faction's submitted description before it is applied.
     * For example, filtering or denying profanity.
     * <p>After this event, the new description will be checked to ensure it is below the configured maximum length</p>
     * <p>
     *     This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. <br>
     *     If the event is cancelled, the faction's description will not change.
     * </p>
     * <p>
     *     When cancelling the event, you should provide a reason with {@link #setDenyReason(Component)} to
     *     allow commands to give a reason for not finishing.<br>
     *     If no reason is given then no feedback will be given to the player
     * </p>
     */
    @Cancelable
    public static class Pre extends FactionChangeDescriptionEvent implements IFactionPreEvent, IFactionEventDenyReason {
        /** The instigator of the action (if there is one) */
        private final ServerPlayer instigator;

        /** A reason for why the event was denied */
        private Component denyReason = null;

        /**
         * @param instigator     The player that instigated the event
         * @param faction        The faction the event is about
         * @param newDescription The description the faction is changing to
         */
        public Pre(final @Nullable ServerPlayer instigator, final @NotNull Faction faction, final String newDescription) {
            super(faction, newDescription);
            this.instigator = instigator;
        }

        /** {@inheritDoc} */
        @Override
        public String getOldDescription() {
            return faction.getDescription();
        }

        /**
         * Set the new description
         * @param newDescription The value to use for the faction's new description
         */
        public void setNewDescription(final String newDescription) {
            this.newDescription = newDescription;
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
     * Fired after the faction description changes
     * <br>
     * The intention of this event is to allow observing description changes to update other resources
     */
    public static class Post extends FactionChangeDescriptionEvent {
        /** The old description of the faction */
        final String oldDescription;

        /**
         * @param faction        The faction the event is about
         * @param newDescription The description the faction is changing to
         */
        public Post(final @NotNull Faction faction, final String newDescription, final String oldDescription) {
            super(faction, newDescription);
            this.oldDescription = oldDescription;
        }

        /** {@inheritDoc} */
        @Override
        public String getOldDescription() {
            return oldDescription;
        }
    }
}
