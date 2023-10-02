package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction is disbanded
 * @see FactionDisbandEvent.Pre
 * @see FactionDisbandEvent.Post
 */
public class FactionDisbandEvent extends FactionEvent {
    /**
     * @param faction The faction the event is about
     */
    protected FactionDisbandEvent(@NotNull final Faction faction) {
        super(faction);
    }

    /**
     * Fired before a faction is disbanded
     * <br>
     * The purpose of this event is to allow preventing the disbanding of a faction, for example if there are some
     * relations that depend on the faction that need to be sorted first
     * <p>
     *     This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.<br>
     *     If the event is cancelled, the faction will not be disbanded
     * </p>
     */
    @Cancelable
    public static class Pre extends FactionDisbandEvent implements IFactionPreEvent {
        /** The instigator of the action (if there is one) */
        private final ServerPlayer instigator;

        /**
         * @param instigator The player that instigated the event
         * @param faction    The faction the event is about
         */
        public Pre(@Nullable final ServerPlayer instigator, @NotNull final Faction faction) {
            super(faction);
            this.instigator = instigator;
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable ServerPlayer getInstigator() {
            return instigator;
        }
    }

    /**
     * Fired after a faction has been disbanded
     * <br>
     * The intention of this event is to allow observing faction disbanding to update other resources
     * <p>
     * Please note that this event is called after the committal to disbanding, but just before the actual disbanding,
     * therefore the players, relations, land claims, and other faction details are intact, but will be removed
     * immediately following this event.
     * </p>
     */
    public static class Post extends FactionDisbandEvent {
        /**
         * @param faction    The faction the event is about
         */
        public Post(@NotNull final Faction faction) {
            super(faction);
        }
    }
}
