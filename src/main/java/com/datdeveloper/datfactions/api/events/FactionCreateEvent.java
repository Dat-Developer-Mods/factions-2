package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction is created
 * @see FactionCreateEvent.Pre
 * @see FactionCreateEvent.Post
 */
public class FactionCreateEvent extends BaseFactionEvent {

    /**
     * @param instigator The CommandSource that instigated the event
     */
    protected FactionCreateEvent(@Nullable final CommandSource instigator) {
        super(instigator);
    }

    /**
     * Fired before a faction is created
     * <br>
     * The purpose of this event is to allow modifying/checking the creation of a faction, for example to profanity
     * check a faction name, or to prevent specific players from creating a faction.
     * <p>
     *     After this event, the new faction's name will be checked to ensure it is below the configured maximum length
     * </p>
     * <p>
     *     This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     *     If the event is cancelled, the faction's description will not change.
     * </p>
     */
    @HasResult
    public static class Pre extends FactionCreateEvent {
        /** The name of the faction */
        String name;

        /**
         * @param instigator The CommandSource that instigated the event
         * @param name       The name for the new faction
         */
        public Pre(@Nullable final CommandSource instigator, final String name) {
            super(instigator);
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
         * @param instigator The CommandSource that instigated the event
         * @param newFaction The newly created faction
         */
        public Post(@Nullable final CommandSource instigator, final Faction newFaction) {
            super(instigator);
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
