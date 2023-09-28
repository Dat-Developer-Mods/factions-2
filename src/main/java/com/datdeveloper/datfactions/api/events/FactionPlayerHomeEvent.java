package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a player goes to the faction home
 * @see FactionPlayerHomeEvent.Pre
 * @see FactionPlayerHomeEvent.Post
 */
public class FactionPlayerHomeEvent extends FactionPlayerEvent {
    /**
     * @param instigator The CommandSource that instigated the event
     * @param player The player the event is for
     */
    protected FactionPlayerHomeEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player) {
        super(instigator, player);
    }

    /**
     * Get the location of the player's faction's home
     * @return The home location
     */
    public BlockPos getFactionHomeLocation() {
        return player.getFaction().getHomeLocation();
    }

    /**
     * Get the level of the player's faction's home
     * @return The home level
     */
    public ResourceKey<Level> getFactionHomeLevel() {
        return player.getFaction().getHomeLevel();
    }

    /**
     * Fired before a player tries to go to the faction home (before the teleport delay)
     * <br>
     * The intention of this event is to allow checking a player before they teleport to their faction home. For
     * example, this could be used to deny a player if they have teleported too many time.
     * <p>
     *     This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.<br>
     *     If the event is cancelled, the player will not go to their faction's home.
     * </p>
     */
    @Cancelable
    public class Pre extends FactionPlayerHomeEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param player     The player the event is for
         */
        protected Pre(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player) {
            super(instigator, player);
        }
    }

    /**
     * Fired after a player goes to their faction's home
     *<br>
     * The intention of this event is to allow observing when a player teleports to their home.
     */
    public class Post extends FactionPlayerHomeEvent {
        /** The player's location before they teleported */
        protected BlockPos previousPosition;

        /** The player's level before they teleported */
        protected ResourceKey<Level> previousLevel;

        /**
         * @param instigator The CommandSource that instigated the event
         * @param player     The player the event is for
         */
        protected Post(@Nullable final CommandSource instigator,
                       @NotNull final FactionPlayer player,
                       final BlockPos previousPosition,
                       final ResourceKey<Level> previousLevel) {
            super(instigator, player);
            this.previousPosition = previousPosition;
            this.previousLevel = previousLevel;
        }

        /**
         * Get the position of the player before they teleported to their faction home
         * @return The previous position of the player
         */
        public BlockPos getPreviousPosition() {
            return previousPosition;
        }

        /**
         * Get the level of the player before they teleported to their faction home
         * @return The previous level of the player
         */
        public ResourceKey<Level> getPreviousLevel() {
            return previousLevel;
        }
    }
}
