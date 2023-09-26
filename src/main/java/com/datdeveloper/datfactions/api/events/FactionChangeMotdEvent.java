package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.commands.CommandSource;
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
     * <br>
     * This event {@linkplain HasResult has a result}.<br>
     * To change the result of this event, use {@link #setResult}. Results are interpreted in the following manner:
     * <ul>
     * <li>Allow - The check will succeed, and the MOTD will be set to the value of newMotd</li>
     * <li>Default - The description will be accepted if it meets the configured maximum length requirements</li>
     * <li>Deny - The check will fail, and the MOTD will not be changed.</li>
     * </ul>
     */
    @HasResult
    public static class Pre extends FactionChangeMotdEvent {
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
    public static class Post extends FactionChangeMotdEvent {
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
