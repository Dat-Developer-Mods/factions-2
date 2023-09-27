package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.commands.CommandSource;
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
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     */
    protected FactionDisbandEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction) {
        super(instigator, faction);
    }

    /**
     * Fired before a faction is disbanded
     * <br>
     * The purpose of this event is to allow preventing the disbanding of a faction, for example if there are some
     * relations that depend on the faction that need to be sorted first
     * <p>
     *     This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
     *     If the event is cancelled, the faction will not be disbanded
     * </p>
     */
    @Cancelable
    public static class Pre extends FactionDisbandEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         */
        public Pre(@Nullable final CommandSource instigator, @NotNull final Faction faction) {
            super(instigator, faction);
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
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         */
        public Post(@Nullable final CommandSource instigator, @NotNull final Faction faction) {
            super(instigator, faction);
        }
    }
}
