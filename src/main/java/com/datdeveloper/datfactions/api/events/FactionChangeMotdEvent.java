package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.server.level.ServerPlayer;
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
     * @param faction The faction the event is about
     * @param newMotd The new MOTD of the faction
     */
    protected FactionChangeMotdEvent(@NotNull final Faction faction, final String newMotd) {
        super(faction);
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
    public static class Pre extends FactionChangeMotdEvent implements IFactionPreEvent {
        /** The instigator of the action (if there is one) */
        private final ServerPlayer instigator;

        /**
         * @param instigator The player that instigated the event
         * @param faction The faction the event is about
         * @param newMotd The new MOTD of the faction
         */
        public Pre(@Nullable final ServerPlayer instigator, @NotNull final Faction faction, final String newMotd) {
            super(faction, newMotd);
            this.instigator = instigator;
        }

        /** {@inheritDoc} */
        @Override
        public String getOldMotd() {
            return faction.getMotd();
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable ServerPlayer getInstigator() {
            return instigator;
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
         * @param faction    The faction the event is about
         * @param newMotd    The new MOTD of the faction
         */
        public Post(@NotNull final Faction faction, final String newMotd, final String oldMotd) {
            super(faction, newMotd);
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
