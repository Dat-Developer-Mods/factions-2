package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction changes its MOTD
 * @see FactionChangeMotdEvent.Pre
 * @see FactionChangeMotdEvent.Post
 */
public abstract class FactionChangeMotdEvent extends FactionEvent {
    /**
     * The new MOTD of the faction
     */
    String newMotd;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param newMotd The new MOTD of the faction
     */
    protected FactionChangeMotdEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final String newMotd) {
        super(instigator, faction);
        this.newMotd = newMotd;
    }

    /**
     * Get the original value of the faction's MOTD
     * @return the previous MOTD
     */
    public abstract String getOldMotd();

    /**
     * Get the new faction MOTD
     * @return the new MOTD
     */
    public String getNewMotd() {
        return newMotd;
    }

    /**
     * Set the new faction MOTD
     * @param newMotd the new MOTD
     */
    public void setNewMotd(final String newMotd) {
        this.newMotd = newMotd;
    }

    /**
     * Fired before the faction MOTD changes
     * <br>
     * The purpose of this event is to allow modifying/checking a faction's submitted MOTD before it is applied. For
     * example, filtering or denying profanity.
     * <p>After this event, the new MOTD will be checked to ensure it is below the configured maximum length</p>
     * <p>
     *     This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.<br>
     *     If the event is cancelled, the faction's MOTD will not change.
     * </p>
     */
    @Cancelable
    public class Pre extends FactionChangeMotdEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction The faction the event is about
         * @param newMotd The new MOTD of the faction
         */
        public Pre(@Nullable final CommandSource instigator, @NotNull final Faction faction, final String newMotd) {
            super(instigator, faction, newMotd);
        }

        /** {@inheritDoc} */
        @Override
        public String getOldMotd() {
            return faction.getMotd();
        }
    }

    /**
     * Fired after a faction changes its MOTD
     * <br>
     * The intention of this event is to allow observing changes to the MOTD to update other resources
     */
    public class Post extends FactionChangeMotdEvent {
        /** The old MOTD of the faction */
        final String oldMotd;

        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param newMotd    The new MOTD of the faction
         */
        public Post(@Nullable final CommandSource instigator, @NotNull final Faction faction, final String newMotd, final String oldMotd) {
            super(instigator, faction, newMotd);
            this.oldMotd = oldMotd;
        }

        /** {@inheritDoc} */
        @Override
        public String getOldMotd() {
            return oldMotd;
        }

        /**
         * You cannot change the MOTD in the post event, this will always throw an exception
         */
        @Override
        public void setNewMotd(final String newMotd) {
            throw new UnsupportedOperationException("You cannot change the MOTD in the post event");
        }
    }
}
