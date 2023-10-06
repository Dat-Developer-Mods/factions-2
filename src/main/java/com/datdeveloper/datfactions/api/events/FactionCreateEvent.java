package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction is created
 * @see FactionCreateEvent.Pre
 * @see FactionCreateEvent.Post
 */
public class FactionCreateEvent extends Event {
    protected FactionCreateEvent() {}

    /**
     * Fired before a faction is created
     * <br>
     * The purpose of this event is to allow modifying/checking the creation of a faction, for example to profanity
     * check a faction name, or to prevent specific players from creating a faction.
     * <p>
     *     After this event, the new faction's name will be checked to ensure it is below the configured maximum length
     * </p>
     * <p>
     *     This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.<br>
     *     If the event is cancelled, the faction's description will not change.
     * </p>
     * <p>
     *     When cancelling the event, you should provide a reason with {@link #setDenyReason(Component)} to
     *     allow commands to give a reason for not finishing.<br>
     *     If no reason is given then no feedback will be given to the player
     * </p>
     */
    @Cancelable
    public static class Pre extends FactionCreateEvent implements IFactionPreEvent, IFactionEventDenyReason {
        /** The instigator of the action (if there is one) */
        private final ServerPlayer instigator;

        /** A reason for why the event was denied */
        private Component denyReason = null;

        /** The name of the faction */
        String name;

        /**
         * @param instigator The player that instigated the event
         * @param name       The name for the new faction
         */
        public Pre(@Nullable final ServerPlayer instigator, final String name) {
            this.instigator = instigator;
            this.name = name;
        }

        /**
         * Get the name for the new faction
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Set the name for the new faction
         * @param name The name for the faction
         */
        public void setName(final String name) {
            this.name = name;
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
     * Fired after a new faction is created
     * <br>
     * The intention of this event is to allow observing when factions are created to update other resources
     */
    public static class Post extends FactionCreateEvent {
        /** The newly created faction */
        final Faction newFaction;

        /**
         * @param newFaction The newly created faction
         */
        public Post(final Faction newFaction) {
            this.newFaction = newFaction;
        }

        /**
         * Get the newly created faction
         * @return The newly created faction
         */
        public Faction getFaction() {
            return newFaction;
        }
    }
}
