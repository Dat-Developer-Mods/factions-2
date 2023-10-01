package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction sets its home
 * @see FactionSetHomeEvent.Pre
 * @see FactionSetHomeEvent.Post
 */
public abstract class FactionSetHomeEvent extends FactionEvent {
    /** The new home level */
    ResourceKey<Level> newHomeLevel;

    /** The new home position */
    BlockPos newHomePos;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param newHomeLevel The level the new home is in
     * @param newHomePos The position of the new home
     */
    protected FactionSetHomeEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final ResourceKey<Level> newHomeLevel, final BlockPos newHomePos) {
        super(instigator, faction);
        this.newHomeLevel = newHomeLevel;
        this.newHomePos = newHomePos;
    }

    /**
     * Get the new home level
     * @return the new home level
     */
    public ResourceKey<Level> getNewHomeLevel() {
        return newHomeLevel;
    }

    /**
     * Get the new home position
     * @return the new home position
     */
    public BlockPos getNewHomePos() {
        return newHomePos;
    }

    /**
     * Get the old home level
     *
     * @return the old home level
     */
    public abstract ResourceKey<Level> getOldHomeLevel();

    /**
     * Get the old home position
     * @return the old home position
     */
    public abstract BlockPos getOldHomePos();

    /**
     * Fired before a faction changes its home location
     * <br>
     * The purpose of this event is to allow modifying/checking when a faction's home changes. For example, redirecting
     * a faction home to a different level.
     * <p>
     *     This event {@linkplain HasResult has a result}.<br>
     *     To change the result of this event, use {@link #setResult}. Results are interpreted in the following manner:
     * </p>
     * <ul>
     *     <li>Allow - The check will succeed, and the faction home will be set</li>
     *     <li>Default - The faction home will be set if the home location is on faction land (and that's required)</li>
     *     <li>Deny - The check will fail, and the faction home will not change</li>
     * </ul>
     */
    @HasResult
    public static class Pre extends FactionSetHomeEvent {
        /**
         * @param instigator   The CommandSource that instigated the event
         * @param faction      The faction the event is about
         * @param newHomeLevel The level the new home is in
         * @param newHomePos   The position of the new home
         */
        public Pre(@Nullable final CommandSource instigator,
                   @NotNull final Faction faction,
                   final ResourceKey<Level> newHomeLevel,
                   final BlockPos newHomePos) {
            super(instigator, faction, newHomeLevel, newHomePos);
        }

        /** {@inheritDoc} */
        @Override
        public ResourceKey<Level> getOldHomeLevel() {
            return faction.getHomeLevel();
        }

        /** {@inheritDoc} */
        @Override
        public BlockPos getOldHomePos() {
            return null;
        }

        /**
         * Set the new home level
         * @param newHomeLevel the new home level
         */
        public void setNewHomeLevel(final ResourceKey<Level> newHomeLevel) {
            this.newHomeLevel = newHomeLevel;
        }

        /**
         * Set the new home pos
         * @param newHomePos The new home pos
         */
        public void setNewHomePos(final BlockPos newHomePos) {
            this.newHomePos = newHomePos;
        }
    }

    /**
     * Fired after a faction's home changes
     * <br>
     * The intention of this event is to allow observing the change in home to update other resources
     */
    public static class Post extends FactionSetHomeEvent {
        /** The old home level */
        ResourceKey<Level> oldHomeLevel;

        /** The old home position */
        BlockPos oldHomePos;

        /**
         * @param instigator   The CommandSource that instigated the event
         * @param faction      The faction the event is about
         * @param newHomeLevel The level the new home is in
         * @param newHomePos   The position of the new home
         */
        public Post(@Nullable final CommandSource instigator,
                       @NotNull final Faction faction,
                       final ResourceKey<Level> oldHomeLevel,
                       final BlockPos oldHomePos,
                       final ResourceKey<Level> newHomeLevel,
                       final BlockPos newHomePos) {
            super(instigator, faction, newHomeLevel, newHomePos);
        }

        /** {@inheritDoc} */
        @Override
        public ResourceKey<Level> getOldHomeLevel() {
            return oldHomeLevel;
        }

        /** {@inheritDoc} */
        @Override
        public BlockPos getOldHomePos() {
            return oldHomePos;
        }
    }
}
